package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.ActivityRequestDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.ActivityMapper;
import com.vitaauxilium.vitaauxilium.models.*;
import com.vitaauxilium.vitaauxilium.repositories.ActivityRepository;
import com.vitaauxilium.vitaauxilium.repositories.CategoryRepository;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ActivityServiceTest {

    private ActivityRepository activityRepository;
    private EnvironmentRepository environmentRepository;
    private CategoryRepository categoryRepository;
    private ActivityMapper activityMapper;
    private ActivityService activityService;

    private User familiar;
    private User idoso;
    private UUID envId;
    private Environment environment;

    @BeforeEach
    void setUp() {
        activityRepository = mock(ActivityRepository.class);
        environmentRepository = mock(EnvironmentRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        activityMapper = Mappers.getMapper(ActivityMapper.class);
        activityService = new ActivityService(activityMapper, activityRepository, environmentRepository, categoryRepository);

        envId = UUID.randomUUID();
        environment = new Environment();
        environment.setId(envId);

        familiar = new User();
        familiar.setId(UUID.randomUUID());
        familiar.setProfile(Profile.FAMILY);
        familiar.setActiveAmbient(envId);

        idoso = new User();
        idoso.setId(UUID.randomUUID());
        idoso.setProfile(Profile.ELDERLY);
        idoso.setActiveAmbient(envId);
    }

    @Test
    @DisplayName("create deve criar atividade vinculada ao ambiente ativo e categoria resolvida para FAMILY")
    void createDeveCriarAtividadeParaFamiliar() {
        Category cat = new Category(UUID.randomUUID(), "MEDICATION");
        when(environmentRepository.findByIdAndMemberId(envId, familiar.getId())).thenReturn(Optional.of(environment));
        when(categoryRepository.findByNameCategoriaIgnoreCase("MEDICATION")).thenReturn(Optional.of(cat));
        when(activityRepository.save(any(Activity.class))).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        ActivityRequestDTO dto = new ActivityRequestDTO(
                null, null, "MEDICATION", "Tomar Losartana", "50mg após café", start, null, null);

        Activity criada = activityService.create(dto, familiar);

        assertEquals("Tomar Losartana", criada.getActivityName());
        assertEquals(environment, criada.getEnvironment());
        assertEquals(cat, criada.getCategory());
        assertEquals(ActivityStatus.NOT_STARTED, criada.getActivityStatus());
        assertTrue(criada.getActive());
    }

    @Test
    @DisplayName("create deve negar acesso quando o usuário não for FAMILY")
    void createDeveNegarParaIdoso() {
        ActivityRequestDTO dto = new ActivityRequestDTO(
                null, null, "MEDICATION", "Tomar Losartana", null, LocalDateTime.now(), null, null);

        assertThrows(AccessDeniedException.class, () -> activityService.create(dto, idoso));
    }

    @Test
    @DisplayName("updateStatus deve permitir que o idoso marque a atividade como FINISHED")
    void updateStatusDevePermitirConclusaoPeloIdoso() {
        UUID actId = UUID.randomUUID();
        Activity existente = new Activity();
        existente.setId(actId);
        existente.setEnvironment(environment);
        existente.setActivityStatus(ActivityStatus.NOT_STARTED);

        when(environmentRepository.findByIdAndMemberId(envId, idoso.getId())).thenReturn(Optional.of(environment));
        when(activityRepository.findByIdAndEnvironment_IdAndActiveTrue(actId, envId)).thenReturn(Optional.of(existente));
        when(activityRepository.save(any(Activity.class))).thenAnswer(inv -> inv.getArgument(0));

        Activity atualizada = activityService.updateStatus(actId, ActivityStatus.FINISHED, idoso);

        assertEquals(ActivityStatus.FINISHED, atualizada.getActivityStatus());
    }

    @Test
    @DisplayName("delete deve realizar exclusão lógica (active = false)")
    void deleteDeveDesativarLogicamente() {
        UUID actId = UUID.randomUUID();
        Activity existente = new Activity();
        existente.setId(actId);
        existente.setActive(true);

        when(environmentRepository.findByIdAndMemberId(envId, familiar.getId())).thenReturn(Optional.of(environment));
        when(activityRepository.findByIdAndEnvironment_IdAndActiveTrue(actId, envId)).thenReturn(Optional.of(existente));

        activityService.delete(actId, familiar);

        assertFalse(existente.getActive());
        verify(activityRepository).save(existente);
    }

    @Test
    @DisplayName("findAll com Pageable deve retornar página de atividades do ambiente ativo")
    void findAllPaginadoDeveRetornarPaginaDeAtividades() {
        Activity act = new Activity();
        act.setId(UUID.randomUUID());
        act.setActivityName("Caminhada");

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Activity> mockPage = new org.springframework.data.domain.PageImpl<>(
                java.util.List.of(act), pageable, 1);

        when(environmentRepository.findByIdAndMemberId(envId, familiar.getId())).thenReturn(Optional.of(environment));
        when(activityRepository.findAllByEnvironment_IdAndActiveTrue(envId, pageable)).thenReturn(mockPage);

        org.springframework.data.domain.Page<Activity> result = activityService.findAll(familiar, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Caminhada", result.getContent().get(0).getActivityName());
    }
}
