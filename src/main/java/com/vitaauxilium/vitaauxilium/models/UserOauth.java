package com.vitaauxilium.vitaauxilium.models;

import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_oauth")
@EntityListeners(AuditingEntityListener.class)
public class UserOauth {

    @Id
    @GeneratedUuidV7
    @Column(name = "oauth_id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "oauth_sub", nullable = false, updatable = false)
    private String oauthSub;

    @OneToOne
    @JoinColumn(name = "oauth_user_id", nullable = false)
    private User oauthUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false)
    private Provider oauthProvider;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private Instant creationDate;
}
