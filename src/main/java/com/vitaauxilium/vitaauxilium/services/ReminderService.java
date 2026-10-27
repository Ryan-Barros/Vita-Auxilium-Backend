package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.ReminderRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.ReminderUpdateDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.ReminderMapper;
import com.vitaauxilium.vitaauxilium.models.*;
import com.vitaauxilium.vitaauxilium.repositories.CategoryRepository;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import com.vitaauxilium.vitaauxilium.repositories.ReminderRepository;
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
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderMapper reminderMapper;
    private final EnvironmentRepository environmentRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Reminder> findAll(User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return reminderRepository.findAllByEnvironment_IdAndIsActiveTrueOrderByDateAsc(env.getId());
    }

    @Transactional(readOnly = true)
    public Page<Reminder> findAll(User user, Pageable pageable) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return reminderRepository.findAllByEnvironment_IdAndIsActiveTrue(env.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public Reminder findById(UUID id, User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        return reminderRepository.findByIdAndEnvironment_IdAndIsActiveTrue(id, env.getId())
                .orElseThrow(() -> new EntityNotFoundException("Lembrete não encontrado"));
    }

    public Reminder create(ReminderRequestDTO dto, User user) {
        Environment env = requireFamilyInActiveEnvironment(user, dto.environmentId());
        Reminder reminder = reminderMapper.toEntity(dto);
        reminder.setEnvironment(env);
        reminder.setCategory(resolveCategory(dto.categoryId(), dto.categoryName()));
        if (reminder.getLoop() == null) {
            reminder.setLoop(false);
        }
        reminder.setCompleted(false);
        reminder.setIsActive(true);
        return reminderRepository.save(reminder);
    }

    public Reminder update(UUID id, ReminderUpdateDTO dto, User user) {
        Environment env = requireMemberInActiveEnvironment(user, null);
        Reminder existing = reminderRepository.findByIdAndEnvironment_IdAndIsActiveTrue(id, env.getId())
                .orElseThrow(() -> new EntityNotFoundException("Lembrete não encontrado"));

        boolean isOnlyCompletionToggle = dto.title() == null
                && dto.description() == null
                && dto.date() == null
                && dto.loop() == null
                && dto.categoryId() == null
                && dto.categoryName() == null
                && dto.completed() != null;

        if (!isOnlyCompletionToggle && user.getProfile() != Profile.FAMILY) {
            throw new AccessDeniedException("Apenas familiares podem editar os dados do lembrete!");
        }

        reminderMapper.updateFromDTO(dto, existing);
        if (dto.categoryId() != null || (dto.categoryName() != null && !dto.categoryName().isBlank())) {
            existing.setCategory(resolveCategory(dto.categoryId(), dto.categoryName()));
        }
        return reminderRepository.save(existing);
    }

    public void delete(UUID id, User user) {
        Environment env = requireFamilyInActiveEnvironment(user, null);
        Reminder existing = reminderRepository.findByIdAndEnvironment_IdAndIsActiveTrue(id, env.getId())
                .orElseThrow(() -> new EntityNotFoundException("Lembrete não encontrado"));
        existing.setIsActive(false);
        reminderRepository.save(existing);
    }

    private Category resolveCategory(UUID categoryId, String categoryName) {
        if (categoryId != null) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
        }
        String normalized = (categoryName == null || categoryName.isBlank())
                ? "HEALTH"
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
            throw new AccessDeniedException("Apenas familiares podem gerenciar lembretes!");
        }
        return requireMemberInActiveEnvironment(user, fallbackEnvId);
    }
}
