package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.mapper.EnvironmentMapper;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import com.vitaauxilium.vitaauxilium.utils.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class EnvironmentService {

    private final EnvironmentRepository environmentRepository;
    private final EnvironmentMapper environmentMapper;
    private final CodeGenerator codeGenerator;

    @Transactional(readOnly = true)
    public List<Environment> findAllByMember(UUID userId) {
        return environmentRepository.findAllByMemberId(userId);
    }

    @Transactional(readOnly = true)
    public Page<Environment> findAllByMember(UUID userId, Pageable pageable) {
        return environmentRepository.findAllByMemberId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Environment findByIdForMember(UUID id, UUID userId) {
        return environmentRepository.findByIdAndMemberId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado!"));
    }

    @Transactional(readOnly = true)
    public Environment findByActiveInviteCode(String rawCode) {
        String normalized = rawCode == null ? "" : rawCode.trim().toUpperCase();
        Environment env = environmentRepository.findEnvironmentByEnvironmentCode(normalized)
                .orElseThrow(() -> new EntityNotFoundException("Código inválido ou expirado"));
        if (!env.isActive() || (env.getExpirationDate() != null && env.getExpirationDate().isBefore(Instant.now()))) {
            throw new EntityNotFoundException("Código inválido ou expirado");
        }
        return env;
    }

    public Environment create(Environment env) {
        env.setEnvironmentCode(codeGenerator.generateUniqueCode("INV"));
        env.setExpirationDate(Instant.now().plus(Duration.ofDays(30)));
        env.setActive(true);
        return environmentRepository.save(env);
    }

    public Environment update(Environment env, EnvironmentUpdateDTO dto) {
        environmentMapper.updateFromDTO(dto, env);
        return environmentRepository.save(env);
    }

    public Environment save(Environment env) {
        return environmentRepository.save(env);
    }

    public void deactivate(Environment env) {
        env.setActive(false);
        environmentRepository.save(env);
    }
}
