package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.AuthRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.AuthResponseDTO;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.UserRepository;
import com.vitaauxilium.vitaauxilium.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthResponseDTO login(AuthRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return new AuthResponseDTO(jwtService.generateToken(user));
    }
}
