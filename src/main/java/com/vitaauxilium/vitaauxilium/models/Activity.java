package com.vitaauxilium.vitaauxilium.models;

import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedUuidV7
    @Column(name = "activity_id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "environment_id", nullable = false, updatable = false)
    @ToString.Exclude
    private Environment environment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Column(name = "activity_name", nullable = false)
    private String activityName;

    @Column(name = "activity_description", nullable = true)
    private String activityDescription;

    @Column(name = "activity_start")
    private LocalDateTime activityStart;

    @Column(name = "activity_end")
    private LocalDateTime activityEnd;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_status", nullable = false)
    private ActivityStatus activityStatus = ActivityStatus.NOT_STARTED;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;
}
