package com.vitaauxilium.vitaauxilium.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa o CpfConverter (AES-256-GCM, IV aleatório de 96 bits por registro).
 * Formato persistido: Base64(IV[12] || ciphertext || tag[16]).
 * Contraponto ao achado #1 da análise de segurança: o conversor antigo usava
 * AES/ECB (determinístico); estes testes provam que o comportamento mudou.
 */
class CpfConverterTest {

    private static final String CPF = "12345678909";
    private static final String CHAVE_256 = Base64.getEncoder().encodeToString(new byte[32]);

    private CpfConverter cpfConverter;

    @BeforeEach
    void setUp() {
        cpfConverter = new CpfConverter(CHAVE_256);
    }

    @Test
    @DisplayName("Round-trip: encriptar e desencriptar deve retornar o CPF original")
    void deveEncriptarEDesencriptarCorretamente() {
        String cifrado = cpfConverter.convertToDatabaseColumn(CPF);

        assertNotEquals(CPF, cifrado);
        assertEquals(CPF, cpfConverter.convertToEntityAttribute(cifrado));
    }

    @Test
    @DisplayName("Valor nulo deve permanecer nulo nas duas direções")
    void valorNuloDevePermanecerNulo() {
        assertNull(cpfConverter.convertToDatabaseColumn(null));
        assertNull(cpfConverter.convertToEntityAttribute(null));
    }

    @Test
    @DisplayName("IV aleatório: o mesmo CPF gera ciphertexts diferentes (corrige a fraqueza do ECB)")
    void mesmoCpfDeveGerarCiphertextsDiferentes() {
        String cifrado1 = cpfConverter.convertToDatabaseColumn(CPF);
        String cifrado2 = cpfConverter.convertToDatabaseColumn(CPF);

        assertNotEquals(cifrado1, cifrado2,
                "Duplicatas não podem ser inferidas comparando ciphertexts");
        assertEquals(CPF, cpfConverter.convertToEntityAttribute(cifrado1));
        assertEquals(CPF, cpfConverter.convertToEntityAttribute(cifrado2));
    }

    @Test
    @DisplayName("Ciphertext deve ser Base64 válido com IV(12) + dado + tag(16)")
    void cifradoDeveTerFormatoEsperado() {
        String cifrado = cpfConverter.convertToDatabaseColumn(CPF);

        byte[] payload = assertDoesNotThrow(() -> Base64.getDecoder().decode(cifrado));
        assertEquals(12 + CPF.length() + 16, payload.length);
    }

    @Test
    @DisplayName("Integridade (AEAD): ciphertext adulterado deve falhar na decifragem")
    void ciphertextAdulteradoDeveFalhar() {
        byte[] payload = Base64.getDecoder().decode(cpfConverter.convertToDatabaseColumn(CPF));
        payload[payload.length - 1] ^= 0x01; // altera 1 bit da tag
        String adulterado = Base64.getEncoder().encodeToString(payload);

        assertThrows(IllegalStateException.class,
                () -> cpfConverter.convertToEntityAttribute(adulterado));
    }

    @Test
    @DisplayName("Chave diferente não decifra o dado")
    void chaveDiferenteNaoDeveDecifrar() {
        String cifrado = cpfConverter.convertToDatabaseColumn(CPF);
        byte[] outraChave = new byte[32];
        outraChave[0] = 1;
        CpfConverter outro = new CpfConverter(Base64.getEncoder().encodeToString(outraChave));

        assertThrows(IllegalStateException.class, () -> outro.convertToEntityAttribute(cifrado));
    }

    @Test
    @DisplayName("Fail-fast: chave com tamanho diferente de 256 bits deve ser rejeitada no startup")
    void chaveInvalidaDeveSerRejeitada() {
        String chave128 = Base64.getEncoder().encodeToString(new byte[16]);

        assertThrows(IllegalStateException.class, () -> new CpfConverter(chave128));
    }
}