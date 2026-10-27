package com.vitaauxilium.vitaauxilium.repositories;

import com.vitaauxilium.vitaauxilium.models.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvironmentRepository extends JpaRepository<Environment, UUID> {
    Optional<Environment> findEnvironmentByEnvironmentCode(String code);

    Boolean existsByEnvironmentCode(String code);

    @Query(value = """
            select e from Environment e
            where e.isActive = true and exists (
                select 1 from EnvironmentMember m
                where m.environment = e and m.user.id = :userId
            )
            """,
            countQuery = """
            select count(e) from Environment e
            where e.isActive = true and exists (
                select 1 from EnvironmentMember m
                where m.environment = e and m.user.id = :userId
            )
            """)
    Page<Environment> findAllByMemberId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
            select e from Environment e
            where e.isActive = true and exists (
                select 1 from EnvironmentMember m
                where m.environment = e and m.user.id = :userId
            )
            """)
    List<Environment> findAllByMemberId(@Param("userId") UUID userId);

    @Query("""
            select e from Environment e
            where e.id = :id and e.isActive = true and exists (
                select 1 from EnvironmentMember m
                where m.environment = e and m.user.id = :userId
            )
            """)
    Optional<Environment> findByIdAndMemberId(@Param("id") UUID id, @Param("userId") UUID userId);
}
