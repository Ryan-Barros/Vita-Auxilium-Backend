package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.ActivityMapper;
import com.vitaauxilium.vitaauxilium.models.Activity;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.ActivityRepository;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityMapper activityMapper;
    private final ActivityRepository activityRepository;
    private final EnvironmentRepository environmentRepository;

    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    public Activity findById(UUID id) {
        return  activityRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Atividade não encontrada"));
    }

    public Activity create(Activity activity, UUID environmentId, User user) {
        Environment env = environmentRepository.findById(environmentId)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado!"));

        boolean isMember = env.getMembers().stream()
                .anyMatch(m -> m.getUser().getId().equals(user.getId()));
        if (!isMember) {
            throw new AccessDeniedException("Você não pertence a este ambiente!");
        }

        activity.setEnvironment(env);
        return activityRepository.save(activity);
    }
}
