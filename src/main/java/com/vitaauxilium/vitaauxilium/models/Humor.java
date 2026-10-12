package com.vitaauxilium.vitaauxilium.models;

import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "humor")
public class Humor {

    @Id
    @GeneratedUuidV7
    @Column(name = "humor_id", unique = true, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    private Activity activity;

    @Column(name = "humor", nullable = false)
    @Enumerated(EnumType.STRING)
    private HumorRange humor = HumorRange.HAPPY;

    @Column(name = "humor_intensity", nullable = false)
    private int intensity;

    @Column(name = "observation")
    private String observation;

    @Column(name = "registration_time")
    private Instant registrationTime;
}
