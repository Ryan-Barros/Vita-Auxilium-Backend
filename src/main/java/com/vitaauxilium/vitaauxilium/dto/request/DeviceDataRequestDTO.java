package com.vitaauxilium.vitaauxilium.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.sql.Timestamp;

public record DeviceDataRequestDTO(
    @NotBlank(message = "TOKEN obrigatório")
    String token,

    @NotBlank(message = "BPM obrigatório")
    int bpm,

    @NotBlank(message = "HRV obrigatório")
    int hrv,

    @NotBlank(message = "TEMPERATURE obrigatório")
    double temperature,

    @NotBlank(message = "OXYGENATION obrigatório")
    int oxygenation,

    @NotBlank(message = "ACCELEROMETER obrigatório")
    int accelerometer,

    @NotBlank(message = "GYROSCOPE obrigatório")
    int gyroscope,

    @NotBlank(message = "MOVEMENT obrigatório")
    int movement,

    @NotBlank(message = "MOVEMENT_LEVEL obrigatório")
    int movement_level,

    @NotBlank(message = "FALL obrigatório")
    int fall,

    @NotBlank(message = "BPM obrigatório")
    int chance_of_fall,

    @NotBlank(message = "BATTERY obrigatório")
    int battery,

    @NotBlank(message = "CHARGING obrigatório")
    int charging,

    @NotBlank(message = "VOLTAGE obrigatório")
    int voltage,

    @NotBlank(message = "DATE obrigatório")
    Timestamp date,

    @NotBlank(message = "WIFIRSSI obrigatório")
    int wifirssi
) {}
