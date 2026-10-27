package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.DeviceUpdateDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.models.*;
import com.vitaauxilium.vitaauxilium.repositories.DeviceDataRepository;
import com.vitaauxilium.vitaauxilium.repositories.DeviceRepository;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import com.vitaauxilium.vitaauxilium.utils.DeviceCrypto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceDataRepository deviceDataRepository;
    private final EnvironmentRepository environmentRepository;
    private final DeviceCrypto crypto;

    @Transactional(readOnly = true)
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Device> findByActiveEnvironment(User user) {
        UUID envId = user.getActiveAmbient();
        if (envId == null) {
            throw new IllegalStateException("Nenhum ambiente ativo selecionado");
        }
        environmentRepository.findByIdAndMemberId(envId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado"));
        return deviceRepository.findByEnvironment_Id(envId);
    }

    @Transactional(readOnly = true)
    public Page<Device> findByActiveEnvironment(User user, Pageable pageable) {
        UUID envId = user.getActiveAmbient();
        if (envId == null) {
            throw new IllegalStateException("Nenhum ambiente ativo selecionado");
        }
        environmentRepository.findByIdAndMemberId(envId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado"));
        return deviceRepository.findAllByEnvironment_Id(envId, pageable);
    }

    @Transactional(readOnly = true)
    public Device findById(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo não encontrado"));
    }

    @Transactional(readOnly = true)
    public Device findByIdForUser(UUID id, User user) {
        Device device = findById(id);
        environmentRepository.findByIdAndMemberId(device.getEnvironment().getId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo não encontrado"));
        return device;
    }

    public Device create(Device device, UUID environmentId, String rawToken) {
        Environment environment = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado"));

        device.setEnvironment(environment);
        device.setTokenHash(crypto.hashToken(rawToken));
        return deviceRepository.save(device);
    }

    public Device createForUser(DeviceRequestDTO dto, User user) {
        if (user.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Apenas familiares podem gerenciar dispositivos!");
        }
        UUID envId = dto.environmentId() != null ? dto.environmentId() : user.getActiveAmbient();
        if (envId == null) {
            throw new IllegalStateException("Nenhum ambiente ativo selecionado");
        }
        Environment environment = environmentRepository.findByIdAndMemberId(envId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado"));

        String rawToken = (dto.tokenHash() != null && !dto.tokenHash().isBlank())
                ? dto.tokenHash().trim()
                : "DEV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 9).toUpperCase();

        Device device = new Device();
        device.setName(dto.name().trim());
        device.setEnvironment(environment);
        device.setTokenHash(crypto.hashToken(rawToken));
        return deviceRepository.save(device);
    }

    public Device updateForUser(UUID id, DeviceUpdateDTO dto, User user) {
        if (user.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Apenas familiares podem editar dispositivos!");
        }
        Device device = findByIdForUser(id, user);
        device.setName(dto.name().trim());
        return deviceRepository.save(device);
    }

    public void deleteForUser(UUID id, User user) {
        if (user.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Apenas familiares podem excluir dispositivos!");
        }
        Device device = findByIdForUser(id, user);
        deviceRepository.delete(device);
    }

    public void delete(UUID id) {
        Device device = findById(id);
        deviceRepository.delete(device);
    }

    @Transactional(readOnly = true)
    public List<DeviceData> getData(Device device) {
        Optional<List<DeviceData>> dadosOpt = deviceDataRepository.findAllByDevice(device);
        if (dadosOpt.isEmpty() || dadosOpt.get().isEmpty()) {
            throw new EntityNotFoundException("Dados do dispositivo não encontrados");
        }
        return dadosOpt.get();
    }
}
