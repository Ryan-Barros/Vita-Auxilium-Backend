package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentMapperTest {

    private final EnvironmentMapper environmentMapper = Mappers.getMapper(EnvironmentMapper.class);

    @Test
    @DisplayName("toEntity a partir do request só deve preencher o nome (código/expiração/status são responsabilidade do EnvironmentService)")
    void toEntityDeveMapearApenasONome() {
        EnvironmentRequestDTO dto = new EnvironmentRequestDTO("Família Souza");

        Environment entidade = environmentMapper.toEntity(dto);

        assertEquals("Família Souza", entidade.getEnvironmentName());
        assertNull(entidade.getEnvironmentCode());
        assertNull(entidade.getExpirationDate());
    }

    @Test
    @DisplayName("updateFromDTO aplica só os campos enviados e preserva código, membros e status")
    void updateFromDTO_AplicaApenasCamposPermitidos() {
        Environment existente = new Environment();
        existente.setEnvironmentName("Nome Antigo");
        existente.setEnvironmentCode("INVAAAAAAAA");
        Instant expiracaoOriginal = Instant.now().plus(30, ChronoUnit.DAYS);
        existente.setExpirationDate(expiracaoOriginal);

        EnvironmentMember membro = new EnvironmentMember();
        existente.addMember(membro);

        // PATCH: só o nome é enviado (nome, expirationDate)
        EnvironmentUpdateDTO dto = new EnvironmentUpdateDTO("Nome Novo", null);

        environmentMapper.updateFromDTO(dto, existente);

        assertEquals("Nome Novo", existente.getEnvironmentName());
        assertEquals("INVAAAAAAAA", existente.getEnvironmentCode());
        assertEquals(expiracaoOriginal, existente.getExpirationDate());
        assertEquals(1, existente.getMembers().size());
        assertTrue(existente.isActive());
    }
}
