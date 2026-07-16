package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceDataRequestDTO;
import com.vitaauxilium.vitaauxilium.mapper.DeviceDataMapper;
import com.vitaauxilium.vitaauxilium.services.DeviceDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device/data")
@RequiredArgsConstructor
public class DeviceDataController {

    private final DeviceDataService deviceDataService;
    private final DeviceDataMapper deviceDataMapper;

    @PostMapping
    public ResponseEntity<Void> createDeviceData(@RequestBody @Valid DeviceDataRequestDTO dto) {
        deviceDataService.create(deviceDataMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
