package com.vitaauxilium.vitaauxilium.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DeviceRequestDTO(
        UUID environmentId,

        @NotBlank(message = "O nome do dispositivo é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
        String name,

        @JsonAlias({"deviceToken", "token", "rawToken"})
        @Size(min = 8, max = 64, message = "O token do dispositivo deve ter entre 8 e 64 caracteres")
        String tokenHash
) {
}
