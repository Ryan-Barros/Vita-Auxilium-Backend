package com.vitaauxilium.vitaauxilium.dto.response;

import java.time.LocalDateTime;
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

        int movementLevel,

        int fall,

        int chanceOfFall,

        LocalDateTime date,

        int wifirssi
) {
}
