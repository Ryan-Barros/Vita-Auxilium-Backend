package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DeviceRequestDTO(
        @NotNull(message = "ID do ambiente não pode ser nulo")
        UUID environmentId,

        @NotBlank(message = "O nome do dispositivo não deve ser nulo")
        @Size(min = 4, message = "Nome muito curto")
        String name,

        @NotBlank(message = "O token do dispositivo não deve ser nulo")
        @Size(min = 13, max = 13, message = "Formato de token incorreto")
        String tokenHash
) {
}
