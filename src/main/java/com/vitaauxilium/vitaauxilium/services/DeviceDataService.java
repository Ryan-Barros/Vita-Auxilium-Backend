package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.response.DeviceDataResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.DeviceDataMapper;
import com.vitaauxilium.vitaauxilium.models.DeviceData;
import com.vitaauxilium.vitaauxilium.repositories.DeviceDataRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceDataService {

    private final DeviceDataRepository deviceDataRepository;
    private final DeviceDataMapper deviceDataMapper;

    public List<DeviceDataResponseDTO> findAll() {
        List<DeviceData> dataList = deviceDataRepository.findAll();
        return deviceDataMapper.toResponseDTOList(dataList);
    }

    public DeviceData findById(UUID id) {
        return deviceDataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dados não encontrados"));
    }

    public DeviceData create(DeviceData data) {
        return deviceDataRepository.save(data);
    }

    public void delete(UUID id) {
        DeviceData data = deviceDataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dados não encontrados"));
        deviceDataRepository.delete(data);
    }

}
