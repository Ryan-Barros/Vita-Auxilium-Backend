package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    Page<Reminder> findAllByEnvironment_IdAndIsActiveTrue(UUID environmentId, Pageable pageable);
    List<Reminder> findAllByEnvironment_IdAndIsActiveTrueOrderByDateAsc(UUID environmentId);
    Optional<Reminder> findByIdAndEnvironment_IdAndIsActiveTrue(UUID id, UUID environmentId);
}
