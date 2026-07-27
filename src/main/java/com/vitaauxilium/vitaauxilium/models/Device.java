package com.vitaauxilium.vitaauxilium.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @OneToOne
    @JoinColumn(name = "environment_id", nullable = false)
    @JsonIgnoreProperties("members")
    private Environment environment;

    @Column(name = "device_name", nullable = false)
    private String name;

    @JsonIgnore
    @Column(name = "device_token_hash", nullable = false, unique = true)
    private String tokenHash;

    @JsonIgnore
    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("device")
    private List<DeviceData> data = new ArrayList<>();
}
