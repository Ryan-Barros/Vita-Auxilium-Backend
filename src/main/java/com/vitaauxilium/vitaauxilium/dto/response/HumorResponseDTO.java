package com.vitaauxilium.vitaauxilium.dto.response;

import com.vitaauxilium.vitaauxilium.models.HumorRange;

import java.time.Instant;
import java.util.UUID;

public record HumorResponseDTO(
        UUID id,
        UUID environmentId,
        UUID userId,
        String userName,
        UUID activityId,
        HumorRange humor,
        String humorLabel,
        int intensity,
        String observation,
        Instant registrationTime
) {
}
