package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    Page<Activity> findAllByEnvironment_IdAndActiveTrue(UUID environmentId, Pageable pageable);
    List<Activity> findAllByEnvironment_IdAndActiveTrueOrderByActivityStartAsc(UUID environmentId);
    Optional<Activity> findByIdAndEnvironment_IdAndActiveTrue(UUID id, UUID environmentId);
}
