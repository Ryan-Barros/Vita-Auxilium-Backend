package com.vitaauxilium.vitaauxilium.security;

import com.vitaauxilium.vitaauxilium.repositories.DeviceRepository;
import com.vitaauxilium.vitaauxilium.utils.DeviceCrypto;
import io.jsonwebtoken.lang.Collections;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class DeviceTokenFilter extends OncePerRequestFilter {

    private final DeviceRepository deviceRepository;
    private final DeviceCrypto crypto;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (!requestURI.startsWith("/device")) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenEnviado = request.getHeader("Authorization");

        if (tokenEnviado == null || tokenEnviado.isBlank()) {
            sendErrorUnauthorized(response, "O header 'Authorization' e obrigatorio.");
            return;
        }

        String tokenWithHashEPepper = crypto.hashToken(tokenEnviado);

        var deviceOpt = deviceRepository.findByTokenHash(tokenWithHashEPepper);

        if (deviceOpt.isEmpty()) {
            sendErrorUnauthorized(response, "Token do dispositivo inválido ou não cadastrado.");
            return;
        }

        var device = deviceOpt.get();

        var authentication = new UsernamePasswordAuthenticationToken(device.getId(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void sendErrorUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }
}