package com.vitaauxilium.vitaauxilium.dto.response;

import com.vitaauxilium.vitaauxilium.models.ActivityStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityResponseDTO(
        UUID id,

        UUID environmentId,

        UUID category,

        String activityName,

        String activityDescription,

        LocalDateTime activityStart,

        LocalDateTime activityEnd,

        ActivityStatus activityStatus,

        Boolean isActive
) {
}
