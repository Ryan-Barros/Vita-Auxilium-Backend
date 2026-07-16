package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.models.UserOauth;
import com.vitaauxilium.vitaauxilium.repositories.UserOauthRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserOauthService {

    private final UserOauthRepository userOauthRepository;

    public UserOauth findByUserIdAndProvider(UUID userId, String provider) {
        return userOauthRepository.findByUserIdAndProvider(userId, provider)
                .orElseThrow(() -> new EntityNotFoundException("Login externo não encontrado!"));
    }

    public UserOauth createOauthAccount(UserOauth userOauth) {
        return userOauthRepository.save(userOauth);
    }
}
