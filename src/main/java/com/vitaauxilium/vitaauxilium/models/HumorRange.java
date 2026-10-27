package com.vitaauxilium.vitaauxilium.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HumorRange {
    VERY_HAPPY("Muito Bem"),
    HAPPY("Bem"),
    NEUTRAL("Neutro"),
    BAD("Mal"),
    VERY_BAD("Muito Mal"),
    SAD("Triste"),
    ANGRY("Nervoso");

    private final String description;
}
