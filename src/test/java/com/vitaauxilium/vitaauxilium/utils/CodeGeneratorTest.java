package com.vitaauxilium.vitaauxilium.utils;

import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CodeGenerator usa SecureRandom (decisão correta, documentada na análise de
 * segurança) e tenta até 10 vezes gerar um código não colidente antes de desistir.
 * Os testes cobrem: formato do código, resolução de colisão via retry, e o
 * caso de exaustão das tentativas.
 */
@ExtendWith(MockitoExtension.class)
class CodeGeneratorTest {

    @Mock
    private EnvironmentRepository environmentRepository;

    private CodeGenerator codeGenerator;

    @BeforeEach
    void setUp() {
        codeGenerator = new CodeGenerator(environmentRepository);
    }

    @Test
    @DisplayName("Código gerado deve ter o prefixo informado e 8 caracteres do alfabeto controlado")
    void deveGerarCodigoComPrefixoEComprimentoCorretos() {
        when(environmentRepository.existsByEnvironmentCode(anyString())).thenReturn(false);

        String code = codeGenerator.generateUniqueCode("INV");

        assertTrue(code.startsWith("INV"));
        assertEquals(3 + 8, code.length());
        // Alfabeto sem caracteres ambíguos (sem 0, O, 1, I) — decisão de UX para
        // ambientes lidos manualmente por familiares/cuidadores.
        assertTrue(code.substring(3).chars()
                .allMatch(c -> "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".indexOf(c) >= 0));
    }

    @Test
    @DisplayName("Deve tentar novamente ao colidir e retornar o primeiro código não existente")
    void deveRetentarQuandoCodigoJaExiste() {
        when(environmentRepository.existsByEnvironmentCode(anyString()))
                .thenReturn(true, true, false); // colide 2x, na 3ª tentativa é livre

        String code = codeGenerator.generateUniqueCode("INV");

        assertNotNull(code);
        verify(environmentRepository, times(3)).existsByEnvironmentCode(anyString());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException após 10 tentativas sempre colidindo")
    void deveLancarExcecaoAposEsgotarTentativas() {
        when(environmentRepository.existsByEnvironmentCode(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> codeGenerator.generateUniqueCode("INV"));
        verify(environmentRepository, times(10)).existsByEnvironmentCode(anyString());
    }
}
