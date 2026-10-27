package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Size(min = 3, max = 45, message = "Nome deve ter entre 3 e 45 caracteres")
        String name,

        @Pattern(regexp = "^\\d{10,11}$", message = "Formato de telefone deve conter 10 ou 11 dígitos")
        String phone,

        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String password
) {
}
