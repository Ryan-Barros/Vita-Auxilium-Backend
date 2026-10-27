package com.vitaauxilium.vitaauxilium.dto.request;

import com.vitaauxilium.vitaauxilium.models.HumorRange;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record HumorRequestDTO(
        UUID environmentId,

        UUID activityId,

        @NotNull(message = "O estado de humor é obrigatório!")
        HumorRange humor,

        @Min(value = 1, message = "A intensidade mínima é 1")
        @Max(value = 5, message = "A intensidade máxima é 5")
        Integer intensity,

        @Size(max = 255, message = "A observação deve ter no máximo 255 caracteres")
        String observation
) {
}
