package com.vitaauxilium.vitaauxilium.dto.response;

import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record EnvironmentResponseDTO(
    UUID id,
    String environmentCode,
    LocalDateTime expirationDate,
    String activeCode,
    List<EnvironmentMember> members
) {}
