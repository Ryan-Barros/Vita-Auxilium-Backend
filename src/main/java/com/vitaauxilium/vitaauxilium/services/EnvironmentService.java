package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.mapper.EnvironmentMapper;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import com.vitaauxilium.vitaauxilium.utils.CodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
    public List<Environment> findAll() {
        return environmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Environment findById(UUID id) {
        return environmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado!"));
    }

    public Environment create(Environment env) {
        env.setEnvironmentCode(codeGenerator.generateUniqueCode("INV"));
        env.setExpirationDate(Instant.now().plus(Duration.ofDays(30)));
        env.setActive(true);
        return environmentRepository.save(env);
    }

    public Environment update(Environment existingEnv, Environment changes) {
        environmentMapper.updateEnvironmentFromEntity(existingEnv, changes);
        return environmentRepository.save(existingEnv);
    }
}
