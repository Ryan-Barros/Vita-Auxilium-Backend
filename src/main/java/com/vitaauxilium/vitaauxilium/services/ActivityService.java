package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.ActivityRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.ActivityUpdateDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.ActivityMapper;
import com.vitaauxilium.vitaauxilium.models.*;
import com.vitaauxilium.vitaauxilium.repositories.ActivityRepository;
import com.vitaauxilium.vitaauxilium.repositories.CategoryRepository;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ActivityService {

    private final ActivityMapper activityMapper;
    private final ActivityRepository activityRepository;
    private final EnvironmentRepository environmentRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Activity> findAll(User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return activityRepository.findAllByEnvironment_IdAndActiveTrueOrderByActivityStartAsc(env.getId());
    }

    @Transactional(readOnly = true)
    public Page<Activity> findAll(User user, Pageable pageable) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return activityRepository.findAllByEnvironment_IdAndActiveTrue(env.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Activity findById(UUID id, User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return activityRepository.findByIdAndEnvironment_IdAndActiveTrue(id, env.getId())
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada"));
    }

    public Activity create(ActivityRequestDTO dto, User user) {
        Environment env = requireFamilyInActiveEnvironment(user, dto.environmentId());
        Activity activity = activityMapper.toEntity(dto);
        activity.setEnvironment(env);
        activity.setCategory(resolveCategory(dto.categoryId(), dto.categoryName()));
        if (activity.getActivityStatus() == null) {
            activity.setActivityStatus(ActivityStatus.NOT_STARTED);
        }
        if (activity.getActivityEnd() == null && activity.getActivityStart() != null) {
            activity.setActivityEnd(activity.getActivityStart().plusHours(1));
        }
        activity.setActive(true);
        return activityRepository.save(activity);
    }

    public Activity update(UUID id, ActivityUpdateDTO dto, User user) {
        Environment env = requireFamilyInActiveEnvironment(user, dto.environmentId());
        Activity existing = activityRepository.findByIdAndEnvironment_IdAndActiveTrue(id, env.getId())
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada"));
        activityMapper.updateFromDTO(dto, existing);
        if (dto.categoryId() != null || (dto.categoryName() != null && !dto.categoryName().isBlank())) {
            existing.setCategory(resolveCategory(dto.categoryId(), dto.categoryName()));
        }
        return activityRepository.save(existing);
    }

    public Activity updateStatus(UUID id, ActivityStatus status, User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        Activity existing = activityRepository.findByIdAndEnvironment_IdAndActiveTrue(id, env.getId())
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada"));
        existing.setActivityStatus(status);
        return activityRepository.save(existing);
    }

    public void delete(UUID id, User user) {
        Environment env = requireFamilyInActiveEnvironment(user, null);
        Activity existing = activityRepository.findByIdAndEnvironment_IdAndActiveTrue(id, env.getId())
                .orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada"));
        existing.setActive(false);
        activityRepository.save(existing);
    }

    private Category resolveCategory(UUID categoryId, String categoryName) {
        if (categoryId != null) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
        }
        String normalized = (categoryName == null || categoryName.isBlank())
                ? "ROUTINE"
                : categoryName.trim().toUpperCase();
        return categoryRepository.findByNameCategoriaIgnoreCase(normalized)
                .orElseGet(() -> {
                    Category newCat = new Category();
                    newCat.setNameCategoria(normalized);
                    return categoryRepository.save(newCat);
                });
    }

    private Environment requireMemberInActiveEnvironment(User user, UUID fallbackEnvId) {
        UUID envId = user.getActiveAmbient() != null ? user.getActiveAmbient() : fallbackEnvId;
        if (envId == null) {
            throw new IllegalStateException("Nenhum ambiente ativo selecionado");
        }
        return environmentRepository.findByIdAndMemberId(envId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado!"));
    }

    private Environment requireFamilyInActiveEnvironment(User user, UUID fallbackEnvId) {
        if (user.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Apenas familiares podem gerenciar atividades!");
        }
        return requireMemberInActiveEnvironment(user, fallbackEnvId);
    }
}
