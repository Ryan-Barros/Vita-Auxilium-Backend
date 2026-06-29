package com.vitaauxilium.vitaauxilium.dto.response;

import java.util.UUID;

public record DeviceResponseDTO(
    UUID environmentId,
    String name
) {}
