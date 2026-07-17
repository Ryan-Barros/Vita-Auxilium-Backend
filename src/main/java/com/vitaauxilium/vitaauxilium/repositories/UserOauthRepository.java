package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Provider;
import com.vitaauxilium.vitaauxilium.models.UserOauth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserOauthRepository extends JpaRepository<UserOauth, UUID> {
    Optional<UserOauth> findByOauthUser_IdAndOauthProvider(UUID oauthUser_id, Provider oauthProvider);
}
