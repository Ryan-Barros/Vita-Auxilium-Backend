package com.vitaauxilium.vitaauxilium.models;

import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
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
    @GeneratedUuidV7
    @Column(name = "device_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "environment_id", nullable = false)
    private UUID environmentId;

    @Column(name = "device_name", nullable = false)
    private String name;

    @Column(name = "device_token_hash", nullable = false, unique = true)
    private String tokenHash;

    @OneToMany(mappedBy = "deviceId", fetch = FetchType.LAZY, cascade =  CascadeType.ALL, orphanRemoval = true)
    private List<DeviceData> data = new ArrayList<>();
}
