package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Device;
import com.vitaauxilium.vitaauxilium.models.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceDataRepository extends JpaRepository<DeviceData, UUID> {
    Optional<List<DeviceData>> findAllByDevice(Device device);
}
