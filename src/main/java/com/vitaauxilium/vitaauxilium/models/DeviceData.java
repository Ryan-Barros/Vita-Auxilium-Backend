package com.vitaauxilium.vitaauxilium.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.sql.Timestamp;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device deviceId;

    @Column(nullable = false, updatable = false)
    private int bpm;

    @Column(nullable = false, updatable = false)
    private int hrv;

    @Column(nullable = false, updatable = false)
    private double temperature;

    @Column(nullable = false, updatable = false)
    private int oxygenation;

    @Column(nullable = false, updatable = false)
    private int accelerometer;

    @Column(nullable = false, updatable = false)
    private int gyroscope;

    @Column(nullable = false, updatable = false)
    private int movement;

    @Column(nullable = false, updatable = false)
    private int movement_level;

    @Column(nullable = false, updatable = false)
    private int fall;

    @Column(nullable = false, updatable = false)
    private int chance_of_fall;

    @Column(nullable = false, updatable = false)
    private int battery;

    @Column(nullable = false, updatable = false)
    private int charging;

    @Column(nullable = false, updatable = false)
    private int voltage;

    @Column(nullable = false, updatable = false)
    private Timestamp date;

    @Column(nullable = false, updatable = false)
    private int wifirssi;
}
