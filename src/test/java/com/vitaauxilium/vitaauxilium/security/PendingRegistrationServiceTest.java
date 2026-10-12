package com.vitaauxilium.vitaauxilium.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * PendingRegistrationService emite um JWT de curtíssima duração (10 min) com o
 * claim "purpose": "oauth_registration", usado para impedir que esse token seja
 * reaproveitado como access_token normal. Os testes cobrem exatamente essa
 * separação de propósito, que é a decisão de segurança central da classe.
 */
class PendingRegistrationServiceTest {

    private PendingRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new PendingRegistrationService();
        String rawSecret = "01234567890123456789012345678901";
        ReflectionTestUtils.setField(service, "secret",
                Base64.getEncoder().encodeToString(rawSecret.getBytes()));
    }

    @Test
    @DisplayName("Deve gerar e validar um token pendente contendo os dados do OAuth")
    void deveGerarEValidarTokenPendente() {
        String token = service.generateToken("novo@vitaauxilium.com", "sub-google-123", "Novo Usuário", "http://foto.png");

        Claims claims = service.parseAndValidate(token);

        assertEquals("novo@vitaauxilium.com", claims.getSubject());
        assertEquals("sub-google-123", claims.get("oauth_sub", String.class));
        assertEquals("Novo Usuário", claims.get("name", String.class));
        assertEquals("http://foto.png", claims.get("picture", String.class));
        assertEquals("oauth_registration", claims.get("purpose", String.class));
    }

    @Test
    @DisplayName("SEGURANÇA: um JWT comum (sem claim 'purpose') não deve ser aceito como token de registro pendente")
    void tokenSemClaimPurposeDeveSerRejeitado() {
        // Simula um JWT assinado com a MESMA chave, mas sem o claim de propósito —
        // por exemplo, um access_token comum vazando para este endpoint.
        String tokenGenerico = io.jsonwebtoken.Jwts.builder()
                .subject("usuario@vitaauxilium.com")
                .signWith((javax.crypto.SecretKey) ReflectionTestUtils.invokeMethod(service, "getSigningKey"))
                .compact();

        assertThrows(JwtException.class, () -> service.parseAndValidate(tokenGenerico));
    }

    @Test
    @DisplayName("Token assinado com chave diferente deve ser rejeitado na verificação de assinatura")
    void tokenComAssinaturaInvalidaDeveSerRejeitado() {
        PendingRegistrationService servicoComOutraChave = new PendingRegistrationService();
        ReflectionTestUtils.setField(servicoComOutraChave, "secret",
                Base64.getEncoder().encodeToString("outra-chave-completamente-diferente-32b".getBytes()));

        String tokenAssinadoComOutraChave = servicoComOutraChave.generateToken(
                "a@a.com", "sub", "Nome", "foto");

        assertThrows(io.jsonwebtoken.security.SignatureException.class,
                () -> service.parseAndValidate(tokenAssinadoComOutraChave));
    }
}
