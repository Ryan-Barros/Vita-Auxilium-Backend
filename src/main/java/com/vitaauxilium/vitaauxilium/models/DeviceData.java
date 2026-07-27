package com.vitaauxilium.vitaauxilium.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Audited
@Entity
@Table(name = "device_data")
public class DeviceData {

    @Id
    @GeneratedUuidV7
    @EqualsAndHashCode.Include
    @Column(name = "device_data_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    @NotAudited
    @JsonIgnoreProperties("data")
    private Device device;

    @Column(nullable = false, updatable = false)
    private int bpm;

    @Column(nullable = false, updatable = false)
    private int bpmMedia;

    @Column(nullable = false, updatable = false)
    private BigDecimal temperature;

    @Column(nullable = false, updatable = false)
    private int oxygenation;

    @Column(nullable = false, updatable = false)
    private int contactSensor;

    @Column(nullable = false, updatable = false)
    private BigDecimal accelerometerX;

    @Column(nullable = false, updatable = false)
    private BigDecimal accelerometerY;

    @Column(nullable = false, updatable = false)
    private BigDecimal accelerometerZ;

    @Column(nullable = false, updatable = false)
    private BigDecimal accelerometer;

    @Column(nullable = false, updatable = false)
    private BigDecimal gyroscopeX;

    @Column(nullable = false, updatable = false)
    private BigDecimal gyroscopeY;

    @Column(nullable = false, updatable = false)
    private BigDecimal gyroscopeZ;

    @Column(nullable = false, updatable = false)
    private BigDecimal gyroscope;

    @Column(nullable = false, updatable = false)
    private BigDecimal movement;

    @Column(nullable = false, updatable = false)
    private int movementLevel;

    @Column(nullable = false, updatable = false)
    private int chanceOfFall;

    @Column(nullable = false, updatable = false)
    private int detectedFall;

    @Column(nullable = false, updatable = false)
    private int alert;

    @Column(nullable = false, updatable = false)
    private int spo2Valid;

    @Column(nullable = false, updatable = false)
    private int bpmValid;

    @Column(nullable = false, updatable = false)
    private int sensorMax30102Heart;

    @Column(nullable = false, updatable = false)
    private int sensorMax30205Temperature;

    @Column(nullable = false, updatable = false)
    private int sensorMpu6050Fall;

    @Column(nullable = false, updatable = false)
    private LocalDateTime date;

    @Column(nullable = false, updatable = false)
    private int wifirssi;

    @Column(nullable = false, updatable = false)
    private int onTime;
}
