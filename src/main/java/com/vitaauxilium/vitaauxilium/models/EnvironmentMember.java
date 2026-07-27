package com.vitaauxilium.vitaauxilium.models;

import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Entity
@Table(name = "environment_members",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_environment_member_user_environment",
                columnNames = {"member_user_id", "member_environment_id"}
        )
)
public class EnvironmentMember {

    @Id
    @GeneratedUuidV7
    @Column(name = "environment_member_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "member_environment_id", nullable = false, updatable = false)
    private Environment environment;
}
