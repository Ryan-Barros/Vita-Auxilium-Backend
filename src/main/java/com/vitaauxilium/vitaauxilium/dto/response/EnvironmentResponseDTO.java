package com.vitaauxilium.vitaauxilium.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EnvironmentResponseDTO(
        UUID id,
        String environmentName,
        String environmentCode,
        Instant expirationDate,
        String activeCode,
        List<EnvironmentMemberResponseDTO> members
) {
}
