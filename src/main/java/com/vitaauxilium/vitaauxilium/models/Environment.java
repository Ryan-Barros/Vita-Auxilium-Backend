package com.vitaauxilium.vitaauxilium.models;

import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.Instant;
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
    @GeneratedUuidV7
    @Column(name = "environment_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "environment_name", nullable = false)
    private String environmentName;

    @Column(name = "environment_code", nullable = false, unique = true)
    private String environmentCode;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;

    @Column(name = "is_active", nullable = false, columnDefinition = "true")
    private boolean isActive;

    @OneToMany(mappedBy = "environment", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnvironmentMember> members = new ArrayList<>();

    public void addMember(EnvironmentMember member) {
        this.members.add(member);
        member.setEnvironment(this);
    }
}
