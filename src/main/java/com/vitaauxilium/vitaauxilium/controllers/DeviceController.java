package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.DeviceUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.DeviceResponseDTO;
import com.vitaauxilium.vitaauxilium.dto.response.PageResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.DeviceMapper;
import com.vitaauxilium.vitaauxilium.models.Device;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<PageResponseDTO<DeviceResponseDTO>> findAllDevices(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        if (user != null && user.getActiveAmbient() != null) {
            Page<Device> page = deviceService.findByActiveEnvironment(user, pageable);
            return ResponseEntity.ok(PageResponseDTO.from(page, deviceMapper::toResponseDTO));
        }
        return ResponseEntity.ok(new PageResponseDTO<>(List.of(), 0, pageable.getPageSize(), 0, 0, true, true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> findDeviceById(@AuthenticationPrincipal User user,
                                                            @PathVariable UUID id) {
        Device entity = deviceService.findByIdForUser(id, user);
        return ResponseEntity.ok(deviceMapper.toResponseDTO(entity));
    }

    @PostMapping
    public ResponseEntity<DeviceResponseDTO> createDevice(@AuthenticationPrincipal User user,
                                                          @RequestBody @Valid DeviceRequestDTO dto) {
        Device created = deviceService.createForUser(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(deviceMapper.toResponseDTO(created));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> updateDevice(@AuthenticationPrincipal User user,
                                                          @PathVariable UUID id,
                                                          @RequestBody @Valid DeviceUpdateDTO dto) {
        Device updated = deviceService.updateForUser(id, dto, user);
        return ResponseEntity.ok(deviceMapper.toResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@AuthenticationPrincipal User user,
                                             @PathVariable UUID id) {
        deviceService.deleteForUser(id, user);
        return ResponseEntity.noContent().build();
    }
}
