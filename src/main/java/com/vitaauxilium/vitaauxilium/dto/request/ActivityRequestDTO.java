package com.vitaauxilium.vitaauxilium.dto.request;

import com.vitaauxilium.vitaauxilium.models.ActivityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityRequestDTO(
        @NotBlank
        UUID environmentId,

        @NotBlank
        UUID categoryId,

        @NotBlank(message = "Nome obrigatório!")
        @Size(min = 3, max = 35, message = "O nome precisa ter entre 3 e 35 caracteres!")
        String activityName,

        String activityDescription,

        @NotBlank(message = "A data de início é obrigatória!")
        LocalDateTime activityStart,

        @NotBlank(message = "A data de fim é obrigatória!")
        LocalDateTime activityEnd,

        @NotBlank(message = "Status da Activity é obrigatório!")
        ActivityStatus activityStatus
) {
}
