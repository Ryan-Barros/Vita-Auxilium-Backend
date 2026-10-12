package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.EnvironmentMapper;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
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
    @DisplayName("updateWithRelation deve lançar AccessDeniedException quando o usuário for FAMILY mas não for membro do ambiente")
    void updateDeveNegarAcessoQuandoFamiliarNaoEhMembro() {
        UUID envId = UUID.randomUUID();
        Environment ambienteSemEsseFamiliar = new Environment();
        ambienteSemEsseFamiliar.setMembers(new ArrayList<>()); // ninguém é membro

        when(environmentService.finenvId)).thenReturn(ambienteSemEsseFamiliar);

        EnvironmentUpdateDTO dto = new EnvironmentUpdateDTO("Novo Nome", null, null);

        assertThrows(AccessDeniedException.class,
                () -> facadeService.updateWithRelation(familiar, dto, envId));
    }

    @Test
    @DisplayName("updateWithRelation deve permitir a atualização quando o usuário é FAMILY e membro do ambiente")
    void updateDevePermitirQuandoFamiliarEhMembro() {
        UUID envId = UUID.randomUUID();

        Environment ambienteExistente = new Environment();
        EnvironmentMember membro = new EnvironmentMember();
        membro.setUser(familiar);
        ambienteExistente.setMembers(new ArrayList<>(java.util.List.of(membro)));

        Environment mudancas = new Environment();
        Environment atualizado = new Environment();

        EnvironmentUpdateDTO dto = new EnvironmentUpdateDTO("Novo Nome", null, null);

        when(environmentService.findById(envId)).thenReturn(ambienteExistente);
        when(environmentMapper.toEntityFromUpdateDTO(dto)).thenReturn(mudancas);
        when(environmentService.update(ambienteExistente, mudancas)).thenReturn(atualizado);

        facadeService.updateWithRelation(familiar, dto, envId);

        verify(environmentService).update(ambienteExistente, mudancas);
        verify(environmentMapper).toResponseDTO(atualizado);
    }
}
