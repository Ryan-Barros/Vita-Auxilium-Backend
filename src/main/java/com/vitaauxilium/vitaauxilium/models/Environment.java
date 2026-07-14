package com.vitaauxilium.vitaauxilium.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Entity
@Table(name = "environments")
public class Environment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "environment_name", nullable = false)
    private String environmentName;

    @Column(name = "environment_code", nullable = false, unique = true)
    private String environmentCode;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(name = "is_active", nullable = false, columnDefinition = "true")
    private boolean isActive;

    @OneToMany(mappedBy = "environment", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnvironmentMember> members = new ArrayList<>();
}
