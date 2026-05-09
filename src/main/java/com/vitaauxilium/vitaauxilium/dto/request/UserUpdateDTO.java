package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
        String name,

        @Size(min = 11, message = "Número de telefone deve ter no mínimo 11 caracteres")
        String phone,

        @Email(message = "E-mail inválido")
        String email,

        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String password
) {}
