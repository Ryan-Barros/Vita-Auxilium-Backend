package com.vitaauxilium.vitaauxilium.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Entity
@Table(name = "ambients")
public class Ambient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ambient_code", nullable = false, unique = true)
    private String ambientCode;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "active_code", nullable = false)
    private String activeCode;

    @Column(name = "is_active", nullable = false, columnDefinition = "true")
    private boolean isActive;
}
