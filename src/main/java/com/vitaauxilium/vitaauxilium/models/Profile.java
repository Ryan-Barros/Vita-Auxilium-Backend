package com.vitaauxilium.vitaauxilium.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Profile {
    FAMILY("Familiar"),
    CAREGIVER("Cuidador"),
    ELDERLY("Idoso");

    private final String description;

    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
