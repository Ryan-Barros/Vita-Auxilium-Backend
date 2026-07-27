package com.vitaauxilium.vitaauxilium.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DeviceDataRequestDTO(
        @NotNull(message = "BPM obrigatório")
        @JsonAlias({"Batimento_Atual"})
        Double bpm,

        @NotNull(message = "AVARAGE_BPM obrigatório")
        @JsonAlias({"Media_Batimento"})
        Integer bpmMedia,

        @NotNull(message = "OXYGENATION obrigatório")
        @JsonAlias({"Oxigenacao"})
        Integer oxygenation,

        @NotNull(message = "TEMPERATURE obrigatório")
        @JsonAlias({"Temperatura"})
        Double temperature,

        @NotNull(message = "CONTACT_SENSOR obrigatório")
        @JsonAlias({"Contato_Sensor"})
        Integer contactSensor,

        @NotNull(message = "ACCELEROMETER_X obrigatório")
        @JsonAlias({"Aceleracao_X"})
        Double accelerometerX,

        @NotNull(message = "ACCELEROMETER_Y obrigatório")
        @JsonAlias({"Aceleracao_Y"})
        Double accelerometerY,

        @NotNull(message = "ACCELEROMETER_Z obrigatório")
        @JsonAlias({"Aceleracao_Z"})
        Double accelerometerZ,

        @NotNull(message = "GYROSCOPE_X obrigatório")
        @JsonAlias({"Giroscopio_X"})
        Double gyroscopeX,

        @NotNull(message = "GYROSCOPE_Y obrigatório")
        @JsonAlias({"Giroscopio_Y"})
        Double gyroscopeY,

        @NotNull(message = "GYROSCOPE_Z obrigatório")
        @JsonAlias({"Giroscopio_Z"})
        Double gyroscopeZ,

        @NotNull(message = "ACCELEROMETER obrigatório")
        @JsonAlias({"Acelerometro"})
        Double accelerometer,

        @NotNull(message = "GYROSCOPE obrigatório")
        @JsonAlias({"Giroscopio"})
        Double gyroscope,

        @NotNull(message = "MOVEMENT obrigatório")
        @JsonAlias({"Movimento"})
        Double movement,

        @NotNull(message = "MOVEMENT_LEVEL obrigatório")
        @JsonAlias({"NivelMovimento"})
        Integer movementLevel,

        @NotNull(message = "CHANCE_OF_FALL obrigatório")
        @JsonAlias({"Possivel_Queda"})
        Integer chanceOfFall,

        @NotNull(message = "DETECTED_FALL obrigatório")
        @JsonAlias({"Queda_Detectada"})
        Integer detectedFall,

        @NotNull(message = "ALERT obrigatório")
        @JsonAlias({"Alerta"})
        Integer alert,

        @NotNull(message = "SPO2_VALID obrigatório")
        @JsonAlias({"Spo2_Valido"})
        Integer spo2Valid,

        @NotNull(message = "BPM_VALID obrigatório")
        @JsonAlias({"BPM_Valido"})
        Integer bpmValid,

        @NotNull(message = "SENSOR_MAX30102_HEART obrigatório")
        @JsonAlias({"SensorMAX30102_Coracao"})
        Integer sensorMax30102Heart,

        @NotNull(message = "SENSOR_MAX30102_TEMPERATURE obrigatório")
        @JsonAlias({"SensorMAX30205_Temperatura"})
        Integer sensorMax30205Temperature,

        @NotNull(message = "SENSOR_MPU6050_FALL obrigatório")
        @JsonAlias({"SensorMPU6050_Queda"})
        Integer sensorMpu6050Fall,

        @NotNull(message = "DATE obrigatório")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        @JsonAlias({"Data"})
        LocalDateTime date,

        @NotNull(message = "WIFIRSSI obrigatório")
        @JsonAlias({"WiFiRSSI"})
        Integer wifirssi,

        @NotNull(message = "ON_TIME obrigatório")
        @JsonAlias({"Tempo_Ligado"})
        Integer onTime
) {
}
