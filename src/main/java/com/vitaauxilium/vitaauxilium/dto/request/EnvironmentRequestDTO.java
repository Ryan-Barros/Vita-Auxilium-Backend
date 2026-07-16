package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnvironmentRequestDTO(
   @NotNull(message = "Nome do ambiente é obrigatório!")
   String environmentName
) {}
