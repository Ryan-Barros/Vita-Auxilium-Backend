package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record DeviceRequestDTO(
    @NotBlank(message = "ID do ambiente não pode ser nulo")
    UUID environmentId,

    @NotBlank(message = "O nome do dispositivo não deve ser nulo")
    String name,

    @NotBlank(message = "O token do dispositivo não deve ser nulo")
    String tokenHash
) {}
