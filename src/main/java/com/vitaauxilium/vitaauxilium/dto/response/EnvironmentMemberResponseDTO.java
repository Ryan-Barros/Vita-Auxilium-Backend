package com.vitaauxilium.vitaauxilium.dto.response;

import java.util.UUID;

public record EnvironmentMemberResponseDTO(
        UUID id,
        UUID userId,
        UUID environmentId
) {
}
