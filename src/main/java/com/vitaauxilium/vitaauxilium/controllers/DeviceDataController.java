package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.DeviceDataRequestDTO;
import com.vitaauxilium.vitaauxilium.mapper.DeviceDataMapper;
import com.vitaauxilium.vitaauxilium.models.DeviceData;
import com.vitaauxilium.vitaauxilium.services.DeviceDataService;
import com.vitaauxilium.vitaauxilium.services.DeviceService;
import com.vitaauxilium.vitaauxilium.utils.LogsMaker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/device/data")
@RequiredArgsConstructor
public class DeviceDataController {

    private final DeviceDataService deviceDataService;
    private final DeviceDataMapper deviceDataMapper;
    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<Void> createDeviceData(
            @AuthenticationPrincipal UUID deviceId,
            @RequestBody @Valid DeviceDataRequestDTO dto) {
        LogsMaker.logInfo(log, "Objeto recebido", dto);
        DeviceData entity = deviceDataMapper.toEntity(dto);
        entity.setDevice(deviceService.findById(deviceId));
        deviceDataService.create(entity);
        LogsMaker.logInfo(log, "DeviceData Criado", entity);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
