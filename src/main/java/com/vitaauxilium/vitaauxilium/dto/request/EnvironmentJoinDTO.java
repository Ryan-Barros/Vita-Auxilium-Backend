package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnvironmentJoinDTO(
        @NotBlank(message = "O código do ambiente é obrigatório")
        @Pattern(regexp = "^INV[A-HJ-NP-Z2-9]{8}$", message = "Código inválido")
        String code
) {
}
