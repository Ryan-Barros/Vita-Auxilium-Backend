package com.vitaauxilium.vitaauxilium.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DeviceRateLimiterService usa Bucket4j 8.x com a API
 * Bandwidth.builder().capacity(20).refillGreedy(15, 1min).build() (documentado
 * em memória do projeto: Bandwidth.classic()/Refill.greedy() não existem nesta versão).
 * O bucket nasce cheio (capacidade 20), então os primeiros 20 consumos devem
 * passar e o 21º deve ser bloqueado até o refill.
 */
class DeviceRateLimiterServiceTest {

    private final DeviceRateLimiterService rateLimiterService = new DeviceRateLimiterService();

    @Test
    @DisplayName("Deve permitir até a capacidade do bucket (20) e bloquear a partir da 21ª requisição")
    void devePermitirAteACapacidadeEBloquearDepois() {
        UUID deviceId = UUID.randomUUID();

        for (int i = 1; i <= 20; i++) {
            assertTrue(rateLimiterService.tryConsume(deviceId), "Falhou na tentativa " + i + " (deveria passar)");
        }

        assertFalse(rateLimiterService.tryConsume(deviceId), "A 21ª tentativa deveria ser bloqueada");
    }

    @Test
    @DisplayName("Dispositivos diferentes devem ter buckets independentes")
    void dispositivosDiferentesDevemTerBucketsIndependentes() {
        UUID deviceA = UUID.randomUUID();
        UUID deviceB = UUID.randomUUID();

        for (int i = 0; i < 20; i++) {
            assertTrue(rateLimiterService.tryConsume(deviceA));
        }
        assertFalse(rateLimiterService.tryConsume(deviceA));

        // deviceB não deve ser afetado pelo esgotamento do bucket de deviceA
        assertTrue(rateLimiterService.tryConsume(deviceB));
    }
}
