package com.vitaauxilium.vitaauxilium.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.vitaauxilium.vitaauxilium.models.Profile;
import jakarta.validation.constraints.*;

public record UserRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, message = "Nome muito curto")
        String name,

        @NotBlank(message = "Número de telefone é obrigatório")
        @Pattern(regexp = "^\\d{10,11}$", message = "Formato de telefone deve conter 10 ou 11 dígitos")
        String phone,

        @NotNull(message = "Perfil é obrigatório")
        Profile profile,

        @Email(message = "E-mail inválido")
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank
        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
        String password
) {
}
