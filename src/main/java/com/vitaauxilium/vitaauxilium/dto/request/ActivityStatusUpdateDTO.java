package com.vitaauxilium.vitaauxilium.dto.request;

import com.vitaauxilium.vitaauxilium.models.ActivityStatus;
import jakarta.validation.constraints.NotNull;

public record ActivityStatusUpdateDTO(
        @NotNull(message = "Status da atividade é obrigatório!")
        ActivityStatus activityStatus
) {
}
