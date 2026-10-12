package com.vitaauxilium.vitaauxilium.models;

import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
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
@Table(name = "reminders")
public class Reminder {

    @Id
    @GeneratedUuidV7
    @Column(name = "reminder_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Environment environment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @Column(name = "reminder_tittle", nullable = false, length = 90)
    private String title;

    @Column(name = "reminder_description")
    private String description;

    @Column(name = "reminder_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "reminder_loop")
    private Boolean loop = false;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
