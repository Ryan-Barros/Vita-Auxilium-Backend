package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Humor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HumorRepository extends JpaRepository<Humor, UUID> {
    Page<Humor> findAllByEnvironment_Id(UUID environmentId, Pageable pageable);
    List<Humor> findAllByEnvironment_IdOrderByRegistrationTimeDesc(UUID environmentId);
    Optional<Humor> findFirstByEnvironment_IdOrderByRegistrationTimeDesc(UUID environmentId);
    Optional<Humor> findByIdAndEnvironment_Id(UUID id, UUID environmentId);
}
