package com.vitaauxilium.vitaauxilium.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityStatus {
    NOT_STARTED("Não Iniciado"),
    RUNNING("Em Andamento"),
    FINISHED("Concluído");

    private final String description;
}
