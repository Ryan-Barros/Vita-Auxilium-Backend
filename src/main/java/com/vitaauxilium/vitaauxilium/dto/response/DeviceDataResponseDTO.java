package com.vitaauxilium.vitaauxilium.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeviceDataResponseDTO(
        UUID id,
        UUID deviceId,
        int bpm,
        int bpmMedia,
        BigDecimal temperature,
        int oxygenation,
        int contactSensor,
        BigDecimal accelerometer,
        BigDecimal gyroscope,
        BigDecimal movement,
        int movementLevel,
        int chanceOfFall,
        int detectedFall,
        int alert,
        LocalDateTime date,
        int wifirssi,
        int onTime
) {
}
