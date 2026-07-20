package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Environment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {
    Optional<Environment> findEnvironmentByEnvironmentCode(String code);

    Boolean existsByEnvironmentCode(String code);
}
