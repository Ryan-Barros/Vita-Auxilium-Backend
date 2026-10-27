package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.AuthRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.OAuthCompleteRegistrationDTO;
import com.vitaauxilium.vitaauxilium.exception.DuplicateResourceException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    private final UserOauthRepository userOauthRepository;
    private final EnvironmentMemberFacadeService environmentMemberFacadeService;

    public String login(AuthRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return jwtService.generateToken(user);
    }

    @Transactional
    public User register(User user) {
        return register(user, null);
    }

    @Transactional
    public User register(User user, String inviteCode) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email já cadastrado");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        if (inviteCode != null && !inviteCode.isBlank() && environmentMemberFacadeService != null) {
            environmentMemberFacadeService.joinByCode(savedUser, inviteCode);
        }

        try {
            emailService.enviarEmailTexto(
                    user.getEmail(),
                    EmailTemplate.WELCOME.getSubject(),
                    EmailTemplate.WELCOME.formatBody(user.getName())
            );
        } catch (Exception e) {
            log.warn("Falha não crítica ao enviar e-mail de boas-vindas");
        }

        return savedUser;
    }

    @Transactional
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
