package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReminderRequestDTO(
        UUID environmentId,

        UUID categoryId,

        String categoryName,

        @NotBlank(message = "O título do lembrete é obrigatório!")
        @Size(min = 2, max = 90, message = "O título deve ter entre 2 e 90 caracteres")
        String title,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String description,

        @NotNull(message = "A data/horário do lembrete é obrigatória!")
        LocalDateTime date,

        Boolean loop
) {
}
