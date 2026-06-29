package com.vitaauxilium.vitaauxilium.dto.response;

import java.sql.Timestamp;
import java.util.UUID;

public record DeviceDataResponseDTO(
    UUID deviceId,

    int bpm,

    int hrv,

    double temperature,

    int oxygenation,

    int accelerometer,

    int gyroscope,

    int movement,

    int movement_level,

    int fall,

    int chance_of_fall,

    int battery,

    int charging,

    int voltage,

    Timestamp date,

    int wifirssi
) {}
