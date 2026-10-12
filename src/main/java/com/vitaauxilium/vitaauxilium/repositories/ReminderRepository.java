package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
}
