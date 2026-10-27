package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
    Optional<Device> findByTokenHash(String hash);
    Optional<Device> findByEnvironment_Id(UUID environmentId);
    Page<Device> findAllByEnvironment_Id(UUID environmentId, Pageable pageable);
    List<Device> findAllByEnvironment_Id(UUID environmentId);
}
