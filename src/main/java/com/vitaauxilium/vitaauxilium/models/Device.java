package com.vitaauxilium.vitaauxilium.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "environment_id", nullable = false)
    private UUID environmentId;

    @Column(name = "device_name", nullable = false)
    private String name;

    @Column(name = "device_token", nullable = false)
    private String token;

    @OneToMany(fetch = FetchType.LAZY, cascade =  CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "data", nullable = false)
    private List<DeviceData> data = new ArrayList<>();
}
