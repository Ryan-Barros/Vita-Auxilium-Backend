package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Número de telefone é obrigatório")
        String phone,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String password
) {}
