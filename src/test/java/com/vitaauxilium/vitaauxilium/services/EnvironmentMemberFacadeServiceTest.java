package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.EnvironmentMapper;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * EnvironmentMemberFacadeService concentra a única checagem de autorização
 * imperativa hoje existente no sistema (documentada na análise de segurança
 * como "o único RBAC real"). Esses testes fixam essa regra de negócio como
 * contrato, para que qualquer refatoração futura de autorização (ex.: migrar
 * para @PreAuthorize) tenha uma base de comparação.
 */
@ExtendWith(MockitoExtension.class)
class EnvironmentMemberFacadeServiceTest {

    @Mock
    private EnvironmentService environmentService;

    @Mock
    private EnvironmentMapper environmentMapper;

    @InjectMocks
    private EnvironmentMemberFacadeService facadeService;

    private User familiar;
    private User cuidador;

    @BeforeEach
    void setUp() {
        familiar = new User();
        familiar.setId(UUID.randomUUID());
        familiar.setProfile(Profile.FAMILY);

        cuidador = new User();
        cuidador.setId(UUID.randomUUID());
        cuidador.setProfile(Profile.CAREGIVER);
    }

    @Test
    @DisplayName("createWithRelation deve lançar AccessDeniedException quando o usuário não for FAMILY")
    void createDeveNegarAcessoParaNaoFamiliar() {
        EnvironmentRequestDTO dto = new EnvironmentRequestDTO("Família Souza");

        assertThrows(AccessDeniedException.class,
                () -> facadeService.createWithRelation(cuidador, dto));

        verifyNoInteractions(environmentService);
    }

    @Test
    @DisplayName("createWithRelation deve criar o ambiente e vincular o FAMILY como membro")
    void createDeveVincularFamiliarComoMembro() {
        EnvironmentRequestDTO dto = new EnvironmentRequestDTO("Família Souza");
        Environment envMapeado = new Environment();
        envMapeado.setMembers(new ArrayList<>());
        Environment envSalvo = new Environment();

        when(environmentMapper.toEntity(dto)).thenReturn(envMapeado);
        when(environmentService.create(envMapeado)).thenReturn(envSalvo);

        facadeService.createWithRelation(familiar, dto);

        assertEquals(1, envMapeado.getMembers().size());
        EnvironmentMember membroAdicionado = envMapeado.getMembers().get(0);
        assertEquals(familiar, membroAdicionado.getUser());
        assertEquals(envMapeado, membroAdicionado.getEnvironment());
        verify(environmentService).create(envMapeado);
        verify(environmentMapper).toResponseDTO(envSalvo);
    }

    @Test
    @DisplayName("updateWithRelation deve lançar AccessDeniedException quando o usuário não for FAMILY")
    void updateDeveNegarAcessoParaNaoFamiliar() {
        EnvironmentUpdateDTO dto = new EnvironmentUpdateDTO("Novo Nome", null);

        assertThrows(AccessDeniedException.class,
                () -> facadeService.updateWithRelation(cuidador, dto, UUID.randomUUID()));

        verifyNoInteractions(environmentService);
    }

    @Test
    @DisplayName("updateWithRelation deve lançar EntityNotFoundException quando o FAMILY não é membro (não revela a existência do ambiente)")
    void updateDeveFalharComNotFoundQuandoFamiliarNaoEhMembro() {
        UUID envId = UUID.randomUUID();
        when(environmentService.findByIdForMember(envId, familiar.getId()))
                .thenThrow(new EntityNotFoundException("Ambiente não encontrado!"));

        EnvironmentUpdateDTO dto = new EnvironmentUpdateDTO("Novo Nome", null);

        assertThrows(EntityNotFoundException.class,
                () -> facadeService.updateWithRelation(familiar, dto, envId));
        verify(environmentService, never()).update(any(), any());
    }

    @Test
    @DisplayName("updateWithRelation deve atualizar quando o usuário é FAMILY e membro do ambiente")
    void updateDevePermitirQuandoFamiliarEhMembro() {
        UUID envId = UUID.randomUUID();
        Environment existente = new Environment();
        Environment atualizado = new Environment();
        EnvironmentUpdateDTO dto = new EnvironmentUpdateDTO("Novo Nome", null);

        when(environmentService.findByIdForMember(envId, familiar.getId())).thenReturn(existente);
        when(environmentService.update(existente, dto)).thenReturn(atualizado);

        facadeService.updateWithRelation(familiar, dto, envId);

        verify(environmentService).update(existente, dto);
        verify(environmentMapper).toResponseDTO(atualizado);
    }

    @Test
    @DisplayName("findAllByUser com Pageable deve retornar PageResponseDTO de ambientes")
    void findAllByUserPaginadoDeveRetornarPageResponseDTO() {
        Environment env = new Environment();
        env.setId(UUID.randomUUID());
        env.setEnvironmentName("Lar Doce Lar");

        com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO dto =
                new com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO(
                        env.getId(), env.getEnvironmentName(), "INV12345678", java.time.Instant.now(), true, java.util.List.of());

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Environment> page = new org.springframework.data.domain.PageImpl<>(
                java.util.List.of(env), pageable, 1);

        when(environmentService.findAllByMember(familiar.getId(), pageable)).thenReturn(page);
        when(environmentMapper.toResponseDTO(env)).thenReturn(dto);

        com.vitaauxilium.vitaauxilium.dto.response.PageResponseDTO<com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO> result =
                facadeService.findAllByUser(familiar, pageable);

        assertEquals(1, result.totalElements());
        assertEquals("Lar Doce Lar", result.content().get(0).environmentName());
    }
}
