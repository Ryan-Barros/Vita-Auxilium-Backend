package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record EnvironmentUpdateDTO(
        @Size(min = 5, message = "O nome do ambiente deve haver no mínimo 5 caracteres")
        String environmentName,

        @Size(min = 12, max = 12, message = "Formato de código inesperado")
        String environmentCode,

        @Future(message = "A data ou horário deve ser superior ao atual instante!")
        Instant expirationDate
) {
}
