package com.vitaauxilium.vitaauxilium.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Entity
@Table(name = "device_data")
public class DeviceData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(nullable = false)
    private int bpm;

    @Column(nullable = false)
    private int hrv;

    @Column(nullable = false)
    private double temperature;

    @Column(nullable = false)
    private int oxigenation;

    @Column(nullable = false)
    private int accelerometer;

    @Column(nullable = false)
    private int gyroscope;

    @Column(nullable = false)
    private int movement;

    @Column(nullable = false)
    private int movement_level;

    @Column(nullable = false)
    private int fall;

    @Column(nullable = false)
    private int chance_of_fall;

    @Column(nullable = false)
    private int battery;

    @Column(nullable = false)
    private int charging;

    @Column(nullable = false)
    private int voltage;

    @Column(nullable = false)
    private Timestamp date;

    @Column(nullable = false)
    private int wifirssi;
}
