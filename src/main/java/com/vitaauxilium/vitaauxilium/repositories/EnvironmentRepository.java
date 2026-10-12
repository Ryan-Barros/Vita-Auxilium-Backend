package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {
    Optional<Environment> findEnvironmentByEnvironmentCode(String code);

    Boolean existsByEnvironmentCode(String code);

    @Query("""
            select e from Environment e
            where exists (
                select 1 from EnvironmentMember m
                where m.environment = e and m.user.id = :userId
            )
            """)
    List<Environment> findAllByMemberId(@Param("userId") UUID userId);

    @Query("""
            select e from Environment e
            where e.id = :id and exists (
                select 1 from EnvironmentMember m
                where m.environment = e and m.user.id = :userId
            )
            """)
    Optional<Environment> findByIdAndMemberId(@Param("id") UUID id, @Param("userId") UUID userId);
}
