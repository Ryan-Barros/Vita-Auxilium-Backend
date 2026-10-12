package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.AuthRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.OAuthCompleteRegistrationDTO;
import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.Provider;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.models.UserOauth;
import com.vitaauxilium.vitaauxilium.repositories.UserOauthRepository;
import com.vitaauxilium.vitaauxilium.repositories.UserRepository;
import com.vitaauxilium.vitaauxilium.security.JwtService;
import com.vitaauxilium.vitaauxilium.security.PendingRegistrationService;
import com.vitaauxilium.vitaauxilium.utils.EmailService;
import com.vitaauxilium.vitaauxilium.utils.EmailTemplate;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PendingRegistrationService pendingRegistrationService;
    public final UserOauthRepository userOauthRepository;

    public String login(AuthRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return jwtService.generateToken(user);
    }

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        try {
            emailService.enviarEmailTexto(
                    user.getEmail(),
                    EmailTemplate.WELCOME.getSubject(),
                    EmailTemplate.WELCOME.formatBody(user.getName())
            );
        } catch (Exception e) {
            System.err.println("Erro ao enviar email de boas-vindas: " + e.getMessage());
        }

        return savedUser;
    }

    public User completeOAuthRegistration(String pendingToken, OAuthCompleteRegistrationDTO dto) {
        Claims claims = pendingRegistrationService.parseAndValidate(pendingToken);

        User user = new User();
        user.setEmail(claims.getSubject());
        user.setName(claims.get("name", String.class));
        user.setPicture(claims.get("picture", String.class));
        user.setProfile(dto.profile());
        user.setPhone(dto.phone());
        User savedUser = userRepository.save(user);

        UserOauth oauth = new UserOauth();
        oauth.setOauthUser(savedUser);
        oauth.setOauthSub(claims.get("oauth_sub", String.class));
        oauth.setOauthProvider(Provider.GOOGLE);
        userOauthRepository.save(oauth);

        return savedUser;
    }
}
