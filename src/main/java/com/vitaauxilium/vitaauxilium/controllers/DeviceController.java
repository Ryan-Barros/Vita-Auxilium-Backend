package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.DeviceResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.DeviceMapper;
import com.vitaauxilium.vitaauxilium.models.Device;
import com.vitaauxilium.vitaauxilium.services.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;

    @GetMapping
    public ResponseEntity<List<DeviceResponseDTO>> findAllDevices() {
        List<Device> entity = deviceService.findAll();
        return ResponseEntity.ok(deviceMapper.toResponseDTOList(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> findDeviceById(@PathVariable UUID id) {
        Device entity = deviceService.findById(id);
        return ResponseEntity.ok(deviceMapper.toResponseDTO(entity));
    }

    @PostMapping
    public ResponseEntity<DeviceResponseDTO> createDevice(@RequestBody @Valid DeviceRequestDTO dto) {
        Device entity = deviceMapper.toEntity(dto);
        deviceService.create(entity, dto.environmentId(), dto.tokenHash());
        return ResponseEntity.ok(deviceMapper.toResponseDTO(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
