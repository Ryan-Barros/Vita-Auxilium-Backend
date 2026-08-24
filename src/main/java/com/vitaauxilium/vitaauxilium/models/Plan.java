package com.vitaauxilium.vitaauxilium.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Plan {
    FREE("Free"),
    STARTER("Starter"),
    PRO("Pro");

    private final String description;
}
