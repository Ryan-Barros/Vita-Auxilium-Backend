package com.vitaauxilium.vitaauxilium.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
public class PendingRegistrationService {
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String email, String sub, String name, String picture) {
        return Jwts.builder()
                .subject(email)
                .claim("sub", sub)
                .claim("name", name)
                .claim("picture", picture)
                .claim("purpose", "oauth_registration") // impede reuso como access_token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseAndValidate(String token) {
        Claims claims = Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload();
        if (!"oauth_registration".equals(claims.get("purpose", String.class))) {
            throw new JwtException("Token com propósito inválido");
        }
        return claims;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
