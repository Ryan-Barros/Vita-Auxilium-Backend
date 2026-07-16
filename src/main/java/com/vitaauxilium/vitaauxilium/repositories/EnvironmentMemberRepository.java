package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnvironmentMemberRepository extends JpaRepository<EnvironmentMember, UUID> {
}
