package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.models.Device;
import com.vitaauxilium.vitaauxilium.models.DeviceData;
import com.vitaauxilium.vitaauxilium.repositories.DeviceDataRepository;
import com.vitaauxilium.vitaauxilium.repositories.DeviceRepository;
import com.vitaauxilium.vitaauxilium.utils.DeviceCrypto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceDataRepository deviceDataRepository;
    private final DeviceCrypto crypto;

    @Transactional(readOnly = true)
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Device findById(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo não encontrado"));
    }

    public Device create(Device device) {
        String hashToken = crypto.hashToken(device.getTokenHash());
        device.setTokenHash(hashToken);
        return deviceRepository.save(device);
    }

    public void delete(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo não encontrado"));
        deviceRepository.delete(device);
    }

    @Transactional(readOnly = true)
    public List<DeviceData> getData(Device device) {
        return deviceDataRepository.findAllByDevice(device)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new EntityNotFoundException("Dados do dispositivo não encontrados"));
    }

}
