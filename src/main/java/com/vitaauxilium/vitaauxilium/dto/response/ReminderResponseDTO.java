package com.vitaauxilium.vitaauxilium.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReminderResponseDTO(
        UUID id,
        UUID environmentId,
        UUID categoryId,
        String categoryName,
        String title,
        String description,
        LocalDateTime date,
        Boolean loop,
        Boolean completed,
        Boolean isActive
) {
}
