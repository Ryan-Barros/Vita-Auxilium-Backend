package com.vitaauxilium.vitaauxilium.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    GOOGLE("Google");

    private final String description;
}
