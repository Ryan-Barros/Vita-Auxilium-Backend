package com.vitaauxilium.vitaauxilium.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CookieFactory foi criado para resolver o achado da análise de segurança em que
 * `cookie.secure` era ignorado por causa de `.secure(true)` hardcoded em
 * AuthController/SecurityConfig, e para consolidar valores de maxAge divergentes.
 * Estes testes fixam esse contrato: o valor de `cookie.secure` deve realmente
 * ser respeitado, e cada cookie deve ter o path/maxAge esperado.
 */
class CookieFactoryTest {

    private CookieFactory cookieFactory;

    @BeforeEach
    void setUp() {
        cookieFactory = new CookieFactory();
        ReflectionTestUtils.setField(cookieFactory, "cookieSecure", true);
        ReflectionTestUtils.setField(cookieFactory, "jwtExpiration", 3_600_000L); // 1h em ms
    }

    @Test
    @DisplayName("buildAccessTokenCookie deve respeitar cookie.secure=true, ser HttpOnly e cobrir path '/'")
    void deveConstruirAccessTokenCookieRespeitandoSecure() {
        ResponseCookie cookie = cookieFactory.buildAccessTokenCookie("jwt-fake");

        assertEquals("access_token", cookie.getName());
        assertEquals("jwt-fake", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("/", cookie.getPath());
        assertEquals("Lax", cookie.getSameSite());
        assertEquals(Duration.ofMillis(3_600_000L), cookie.getMaxAge());
    }

    @Test
    @DisplayName("cookie.secure=false (perfil dev) deve refletir em secure=false — resolve o achado do relatório")
    void deveRespeitarCookieSecureFalseEmPerfilDev() {
        ReflectionTestUtils.setField(cookieFactory, "cookieSecure", false);

        ResponseCookie cookie = cookieFactory.buildAccessTokenCookie("jwt-fake");

        assertFalse(cookie.isSecure());
    }

    @Test
    @DisplayName("buildExpiredAccessTokenCookie deve zerar o valor e o maxAge (usado no logout)")
    void deveConstruirCookieExpiradoDeAccessToken() {
        ResponseCookie cookie = cookieFactory.buildExpiredAccessTokenCookie();

        assertEquals("", cookie.getValue());
        assertEquals(Duration.ZERO, cookie.getMaxAge());
    }

    @Test
    @DisplayName("buildPendingRegistrationCookie deve ter path restrito a /auth e TTL de 10 minutos")
    void deveConstruirPendingRegistrationCookieComEscopoRestrito() {
        ResponseCookie cookie = cookieFactory.buildPendingRegistrationCookie("token-pendente");

        assertEquals("pending_registration", cookie.getName());
        assertEquals("/auth", cookie.getPath());
        assertEquals(Duration.ofMinutes(10), cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    @DisplayName("buildExpiredPendingRegistrationCookie deve expirar imediatamente no mesmo path")
    void deveConstruirPendingRegistrationCookieExpirado() {
        ResponseCookie cookie = cookieFactory.buildExpiredPendingRegistrationCookie();

        assertEquals("/auth", cookie.getPath());
        assertEquals(Duration.ZERO, cookie.getMaxAge());
    }
}
