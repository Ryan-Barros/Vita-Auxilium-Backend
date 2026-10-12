package com.vitaauxilium.vitaauxilium.security;

import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtService assina com HS256 usando um secret Base64 (${jwt.secret}). Os testes
 * usam ReflectionTestUtils para injetar um secret de teste válido (>=256 bits),
 * exatamente como a análise de segurança recomenda validar no startup real.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private User usuarioFamiliar;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // 32 bytes ASCII = 256 bits, atende ao requisito mínimo do HS256
        String rawSecret = "01234567890123456789012345678901";
        String base64Secret = Base64.getEncoder().encodeToString(rawSecret.getBytes());

        ReflectionTestUtils.setField(jwtService, "secret", base64Secret);
        ReflectionTestUtils.setField(jwtService, "expiration", 3_600_000L); // 1 hora

        usuarioFamiliar = new User();
        usuarioFamiliar.setId(UUID.randomUUID());
        usuarioFamiliar.setEmail("familia@vitaauxilium.com");
        usuarioFamiliar.setName("Ricardo Silva");
        usuarioFamiliar.setPhone("11999999999");
        usuarioFamiliar.setProfile(Profile.FAMILY);
        usuarioFamiliar.setPassword("hash-fake");
        usuarioFamiliar.setActive(true);
    }

    @Test
    @DisplayName("Token gerado deve ter o e-mail do usuário como subject")
    void deveGerarTokenComEmailComoSubject() {
        String token = jwtService.generateToken(usuarioFamiliar);

        assertNotNull(token);
        assertEquals(usuarioFamiliar.getEmail(), jwtService.extractEmail(token));
    }

    @Test
    @DisplayName("Token recém-gerado deve ser válido para o mesmo usuário")
    void deveConsiderarTokenValidoParaOMesmoUsuario() {
        String token = jwtService.generateToken(usuarioFamiliar);

        assertTrue(jwtService.isTokenValid(token, usuarioFamiliar));
    }

    @Test
    @DisplayName("Token deve ser inválido quando o e-mail não corresponde ao UserDetails informado")
    void deveConsiderarTokenInvalidoParaUsuarioDiferente() {
        String token = jwtService.generateToken(usuarioFamiliar);

        User outroUsuario = new User();
        outroUsuario.setEmail("cuidador@vitaauxilium.com");
        outroUsuario.setProfile(Profile.CAREGIVER);

        assertFalse(jwtService.isTokenValid(token, outroUsuario));
    }

    @Test
    @DisplayName("DECISÃO ARQUITETURAL: token expirado lança ExpiredJwtException na própria extração do e-mail " +
            "(JJWT falha ao fazer parse), por isso JwtAuthFilter precisa capturar JwtException explicitamente")
    void tokenExpiradoDeveLancarExcecaoAoInvesDeRetornarFalse() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // já nasce expirado
        String tokenExpirado = jwtService.generateToken(usuarioFamiliar);

        assertThrows(ExpiredJwtException.class,
                () -> jwtService.isTokenValid(tokenExpirado, usuarioFamiliar));
    }

    @Test
    @DisplayName("O claim 'role' deve refletir o ROLE_ do perfil do usuário")
    void tokenDeveConterRoleDoPerfil() {
        String token = jwtService.generateToken(usuarioFamiliar);

        // Extraímos manualmente para checar o claim customizado 'role'
        io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) ReflectionTestUtils.invokeMethod(jwtService, "getSigningKey"))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("ROLE_FAMILY", claims.get("role", String.class));
    }
}
