package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequestDTO(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "E-mail Inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve possuir no mínimo 6 caracteres")
        String password
) {}
