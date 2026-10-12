package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.models.Environment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    @DisplayName(
            "CONTRATO CORRETO do mapper (para contraste com o bug de EnvironmentService.update): " +
                    "quando chamado na ordem (source=mudanças, target=existente), o mapper realmente sobrepõe " +
                    "os campos não nulos de 'mudanças' sobre 'existente', ignorando nulos."
    )
    void updateEnvironmentFromEntity_QuandoUsadoNaOrdemCorreta_AplicaAsMudancas() {
        Environment existente = new Environment();
        existente.setEnvironmentName("Nome Antigo");
        existente.setEnvironmentCode("INVAAAAAAAA");
        existente.setExpirationDate(Instant.now().plus(30, ChronoUnit.DAYS));

        Environment mudancas = new Environment();
        mudancas.setEnvironmentName("Nome Novo");
        // environmentCode e expirationDate ficam nulos em 'mudancas' de propósito

        // Ordem CORRETA: (source = mudancas, target = existente)
        environmentMapper.updateEnvironmentFromEntity(mudancas, existente);

        assertEquals("Nome Novo", existente.getEnvironmentName());
        assertEquals("INVAAAAAAAA", existente.getEnvironmentCode(), "Nulo em 'mudancas' não deve sobrescrever");
    }
}
