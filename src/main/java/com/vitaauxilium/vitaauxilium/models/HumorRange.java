package com.vitaauxilium.vitaauxilium.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HumorRange {
    HAPPY("Feliz"),
    NEUTRAL("Neutro"),
    SAD("Triste"),
    ANGRY("Nervoso");

    private final String description;
}
