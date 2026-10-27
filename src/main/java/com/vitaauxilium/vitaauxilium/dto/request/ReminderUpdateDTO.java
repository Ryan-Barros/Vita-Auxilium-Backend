package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReminderUpdateDTO(
        UUID categoryId,

        String categoryName,

        @Size(min = 2, max = 90, message = "O título deve ter entre 2 e 90 caracteres")
        String title,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String description,

        LocalDateTime date,

        Boolean loop,

        Boolean completed
) {
}
