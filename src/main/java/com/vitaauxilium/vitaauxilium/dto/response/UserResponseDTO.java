package com.vitaauxilium.vitaauxilium.dto.response;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String phone,
        String email,
        String profile,
        UUID activeAmbient
) {
    public UserResponseDTO(String name, String phone, String email, String profile) {
        this(null, name, phone, email, profile, null);
    }
}
