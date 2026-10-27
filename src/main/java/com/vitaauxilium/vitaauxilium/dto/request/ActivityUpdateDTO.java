package com.vitaauxilium.vitaauxilium.dto.request;

import com.vitaauxilium.vitaauxilium.models.ActivityStatus;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivityUpdateDTO(
        UUID environmentId,

        UUID categoryId,

        String categoryName,

        @Size(min = 3, max = 100, message = "O nome precisa ter entre 3 e 100 caracteres!")
        String activityName,

        String activityDescription,

        LocalDateTime activityStart,

        LocalDateTime activityEnd,

        ActivityStatus activityStatus
) {
    @AssertTrue(message = "A data de término deve ser igual ou posterior à data de início!")
    public boolean isEndValid() {
        return activityStart == null || activityEnd == null || !activityEnd.isBefore(activityStart);
    }
}
