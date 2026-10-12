package com.vitaauxilium.vitaauxilium.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DeviceCrypto é responsável por transformar o token bruto do dispositivo IoT
 * (enviado via header Authorization) em um hash HMAC-SHA256 com pepper de servidor,
 * usado depois em DeviceRepository.findByTokenHash().
 * <p>
 * Escolha de teste: bcrypt não é usado aqui (documentado em memória do projeto)
 * justamente porque HMAC é determinístico e permite lookup indexado. Os testes
 * abaixo validam essa propriedade central (determinismo) e as bordas.
 */
class DeviceCryptoTest {

    private DeviceCrypto deviceCrypto;

    @BeforeEach
    void setUp() {
        deviceCrypto = new DeviceCrypto();
        ReflectionTestUtils.setField(deviceCrypto, "pepper", "pepper-de-teste-super-secreto");
    }

    @Test
    @DisplayName("Mesmo token bruto deve sempre gerar o mesmo hash (determinismo necessário para lookup)")
    void deveGerarHashDeterministico() {
        String hash1 = deviceCrypto.hashToken("token-do-dispositivo-123");
        String hash2 = deviceCrypto.hashToken("token-do-dispositivo-123");

        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Tokens diferentes devem gerar hashes diferentes")
    void devsGerarHashesDiferentesParaTokensDiferentes() {
        String hash1 = deviceCrypto.hashToken("token-A");
        String hash2 = deviceCrypto.hashToken("token-B");

        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("O mesmo token com peppers diferentes deve gerar hashes diferentes (efeito do pepper)")
    void peppersDiferentesDevemGerarHashesDiferentes() {
        String hashComPepperA = deviceCrypto.hashToken("token-fixo");

        DeviceCrypto outraInstancia = new DeviceCrypto();
        ReflectionTestUtils.setField(outraInstancia, "pepper", "outro-pepper-completamente-diferente");
        String hashComPepperB = outraInstancia.hashToken("token-fixo");

        assertNotEquals(hashComPepperA, hashComPepperB);
    }

    @Test
    @DisplayName("Token nulo deve lançar IllegalArgumentException")
    void tokenNuloDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> deviceCrypto.hashToken(null));
    }

    @Test
    @DisplayName("Token em branco deve lançar IllegalArgumentException")
    void tokenEmBrancoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> deviceCrypto.hashToken("   "));
    }

    @Test
    @DisplayName("O hash retornado deve estar em Base64 válido (compatível com coluna VARCHAR)")
    void hashDeveEstarEmBase64Valido() {
        String hash = deviceCrypto.hashToken("token-qualquer");
        assertDoesNotThrow(() -> java.util.Base64.getDecoder().decode(hash));
    }
}
