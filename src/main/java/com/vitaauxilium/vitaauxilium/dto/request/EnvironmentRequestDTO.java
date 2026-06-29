package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EnvironmentRequestDTO(
   @NotBlank(message = "Nome do ambiente é obrigatório!")
   String environmentName
) {}
