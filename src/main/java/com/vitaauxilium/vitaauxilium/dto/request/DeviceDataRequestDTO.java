package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public record DeviceDataRequestDTO(
        @NotNull(message = "BPM obrigatório")
        Integer bpm,

        @NotNull(message = "HRV obrigatório")
        Integer hrv,

        @NotNull(message = "TEMPERATURE obrigatório")
        Double temperature,

        @NotNull(message = "OXYGENATION obrigatório")
        Integer oxygenation,

        @NotNull(message = "ACCELEROMETER obrigatório")
        Integer accelerometer,

        @NotNull(message = "GYROSCOPE obrigatório")
        Integer gyroscope,

        @NotNull(message = "MOVEMENT obrigatório")
        Integer movement,

        @NotNull(message = "MOVEMENT_LEVEL obrigatório")
        Integer movement_level,

        @NotNull(message = "FALL obrigatório")
        Integer fall,

        @NotNull(message = "CHANCE_OF_FALL obrigatório")
        Integer chance_of_fall,

        @NotNull(message = "BATTERY obrigatório")
        Integer battery,

        @NotNull(message = "CHARGING obrigatório")
        Integer charging,

        @NotNull(message = "VOLTAGE obrigatório")
        Integer voltage,

        @NotNull(message = "DATE obrigatório")
        Timestamp date,

        @NotNull(message = "WIFIRSSI obrigatório")
        Integer wifirssi
) {
}
