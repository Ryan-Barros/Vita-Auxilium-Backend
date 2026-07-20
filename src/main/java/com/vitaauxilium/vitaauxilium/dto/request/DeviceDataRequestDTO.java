package com.vitaauxilium.vitaauxilium.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

public record DeviceDataRequestDTO(
        @NotNull(message = "BPM obrigatório")
        @JsonAlias({"Batimento_Atual", "BPM"})
        Double bpm,

        @NotNull(message = "AVARAGE_BPM obrigatório")
        @JsonAlias({"Media_Batimento", "AVARAGE_MEDIA"})
        Integer bpmMedia,

        @NotNull(message = "OXYGENATION obrigatório")
        @JsonAlias({"Oxigenacao", "OXYGENATION"})
        Integer oxygenation,

        @NotNull(message = "TEMPERATURE obrigatório")
        @JsonAlias({"Temperatura", "TEMPERATURE"})
        Double temperature,

        @NotNull(message = "CONTACT_SENSOR obrigatório")
        @JsonAlias({"Contato_Sensor", "CONTACT_SENSOR"})
        Integer contactSensor,

        @NotNull(message = "ACCELEROMETER_X obrigatório")
        @JsonAlias({"Aceleracao_X", "ACCELEROMETER_X"})
        Double accelerometerX,

        @NotNull(message = "ACCELEROMETER_Y obrigatório")
        @JsonAlias({"Aceleracao_Y", "ACCELEROMETER_Y"})
        Double accelerometerY,

        @NotNull(message = "ACCELEROMETER_Z obrigatório")
        @JsonAlias({"Aceleracao_Z", "ACCELEROMETER_Z"})
        Double accelerometerZ,

        @NotNull(message = "GYROSCOPE_X obrigatório")
        @JsonAlias({"Giroscopio_X", "GYROSCOPE_X"})
        Double gyroscopeX,

        @NotNull(message = "GYROSCOPE_Y obrigatório")
        @JsonAlias({"Giroscopio_Y", "GYROSCOPE_Y"})
        Double gyroscopeY,

        @NotNull(message = "GYROSCOPE_Z obrigatório")
        @JsonAlias({"Giroscopio_Z", "GYROSCOPE_Z"})
        Double gyroscopeZ,

        @NotNull(message = "ACCELEROMETER obrigatório")
        @JsonAlias({"Acelerometro", "ACCELEROMETER"})
        Double accelerometer,

        @NotNull(message = "GYROSCOPE obrigatório")
        @JsonAlias({"Giroscopio", "GYROSCOPE"})
        Double gyroscope,

        @NotNull(message = "MOVEMENT obrigatório")
        @JsonAlias({"Movimento", "MOVEMENT"})
        Double movement,

        @NotNull(message = "MOVEMENT_LEVEL obrigatório")
        @JsonAlias({"NivelMovimento", "MOVEMENT_LEVEL"})
        Integer movement_level,

        @NotNull(message = "CHANCE_OF_FALL obrigatório")
        @JsonAlias({"Possivel_Queda", "CHANCE_OF_FALL"})
        Integer chance_of_fall,

        @NotNull(message = "DETECTED_FALL obrigatório")
        @JsonAlias({"Queda_Detectada", "DETECTED_FALL"})
        Integer detectedFall,

        @NotNull(message = "ALERT obrigatório")
        @JsonAlias({"Alerta", "ALERT"})
        Integer alert,

        @NotNull(message = "SPO2_VALID obrigatório")
        @JsonAlias({"Spo2_Valido", "SPO2_VALID"})
        Integer spo2Valid,

        @NotNull(message = "BPM_VALID obrigatório")
        @JsonAlias({"BPM_Valido", "BPM_VALID"})
        Integer bpmValid,

        @NotNull(message = "SENSOR_MAX30102_HEART obrigatório")
        @JsonAlias({"SensorMAX30102_Coracao", "SENSOR_MAX30102_HEART"})
        Integer sensorMax30102Heart,

        @NotNull(message = "SENSOR_MAX30102_TEMPERATURE obrigatório")
        @JsonAlias({"SensorMAX30102_Temperatura", "SENSOR_MAX30102_TEMPERATURE"})
        Integer sensorMax30205Temperature,

        @NotNull(message = "SENSOR_MPU6050_FALL obrigatório")
        @JsonAlias({"SensorMPU6050_Queda", "SENSOR_MPU6050_FALL"})
        Integer sensorMpu6050Fall,

        @NotNull(message = "DATE obrigatório")
        @JsonAlias({"Data", "DATE"})
        Timestamp date,

        @NotNull(message = "WIFIRSSI obrigatório")
        @JsonAlias({"WiFiRSSI", "WIFIRSSI"})
        Integer wifirssi,

        @NotNull(message = "ON_TIME obrigatório")
        @JsonAlias({"Tempo_Ligado", "ON_TIME"})
        Integer onTime
) {
}
