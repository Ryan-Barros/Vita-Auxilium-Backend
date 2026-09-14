package com.vitaauxilium.vitaauxilium.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieFactory {

    @Value("${cookie.secure}")
    private boolean cookieSecure;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public ResponseCookie buildAccessTokenCookie(String jwt) {
        return ResponseCookie.from("access_token", jwt)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(jwtExpiration))
                .build();
    }

    public ResponseCookie buildExpiredAccessTokenCookie() {
        return ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

    public ResponseCookie buildPendingRegistrationCookie(String token) {
        return ResponseCookie.from("pending_registration", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/auth")
                .maxAge(Duration.ofMinutes(10))
                .build();
    }

    public ResponseCookie buildExpiredPendingRegistrationCookie() {
        return ResponseCookie.from("pending_registration", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/auth")
                .maxAge(0)
                .build();
    }
}
