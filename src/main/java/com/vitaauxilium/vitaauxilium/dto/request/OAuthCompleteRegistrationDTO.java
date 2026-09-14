package com.vitaauxilium.vitaauxilium.dto.request;

import com.vitaauxilium.vitaauxilium.models.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OAuthCompleteRegistrationDTO(
        @NotNull(message = "Perfil é obrigatório")
        Profile profile,

        @NotBlank(message = "Número de telefone é obrigatório")
        @Pattern(regexp = "^\\d{10,11}$", message = "Formato de telefone deve conter 10 ou 11 dígitos")
        String phone
) {
}
