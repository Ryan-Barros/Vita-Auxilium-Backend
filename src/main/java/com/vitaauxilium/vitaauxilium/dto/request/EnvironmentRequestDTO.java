package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnvironmentRequestDTO(
        @NotBlank(message = "Nome do ambiente é obrigatório!")
        @Size(min = 5, message = "Nome muito curto")
        String environmentName
) {
}
