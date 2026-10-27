package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.HumorRequestDTO;
import com.vitaauxilium.vitaauxilium.mapper.HumorMapper;
import com.vitaauxilium.vitaauxilium.models.*;
import com.vitaauxilium.vitaauxilium.repositories.ActivityRepository;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import com.vitaauxilium.vitaauxilium.repositories.HumorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class HumorService {

    private final HumorRepository humorRepository;
    private final HumorMapper humorMapper;
    private final EnvironmentRepository environmentRepository;
    private final ActivityRepository activityRepository;

    @Transactional(readOnly = true)
    public List<Humor> findAll(User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return humorRepository.findAllByEnvironment_IdOrderByRegistrationTimeDesc(env.getId());
    }

    @Transactional(readOnly = true)
    public Page<Humor> findAll(User user, Pageable pageable) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return humorRepository.findAllByEnvironment_Id(env.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Humor> findLatest(User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return humorRepository.findFirstByEnvironment_IdOrderByRegistrationTimeDesc(env.getId());
    }

    public Humor create(HumorRequestDTO dto, User user) {
        Environment env = requireMemberInActiveEnvironment(user, dto.environmentId());
        Humor humor = humorMapper.toEntity(dto);
        humor.setEnvironment(env);
        humor.setUser(user);
        humor.setRegistrationTime(Instant.now());
        humor.setIntensity(dto.intensity() != null ? dto.intensity() : mapDefaultIntensity(dto.humor()));

        if (dto.activityId() != null) {
            Activity act = activityRepository.findByIdAndEnvironment_IdAndActiveTrue(dto.activityId(), env.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada no ambiente"));
            humor.setActivity(act);
        }

        return humorRepository.save(humor);
    }

    public void delete(UUID id, User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        Humor existing = humorRepository.findByIdAndEnvironment_Id(id, env.getId())
                .orElseThrow(() -> new EntityNotFoundException("Registro de humor não encontrado"));
        humorRepository.delete(existing);
    }

    private int mapDefaultIntensity(HumorRange range) {
        if (range == null) return 3;
        return switch (range) {
            case VERY_HAPPY -> 5;
            case HAPPY -> 4;
            case NEUTRAL -> 3;
            case BAD, SAD -> 2;
            case VERY_BAD, ANGRY -> 1;
        };
    }

    private Environment requireMemberInActiveEnvironment(User user, UUID fallbackEnvId) {
        UUID envId = user.getActiveAmbient() != null ? user.getActiveAmbient() : fallbackEnvId;
        if (envId == null) {
            throw new IllegalStateException("Nenhum ambiente ativo selecionado");
        }
        return environmentRepository.findByIdAndMemberId(envId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado!"));
    }
}
