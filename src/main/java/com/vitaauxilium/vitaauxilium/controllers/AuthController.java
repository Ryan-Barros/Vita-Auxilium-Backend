package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.AuthRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.OAuthCompleteRegistrationDTO;
import com.vitaauxilium.vitaauxilium.dto.request.UserRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.UserResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.security.JwtService;
import com.vitaauxilium.vitaauxilium.services.AuthService;
import com.vitaauxilium.vitaauxilium.utils.CookieFactory;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;
    private final CookieFactory cookieFactory;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AuthRequestDTO dto, HttpServletResponse response) {
        String token = authService.login(dto);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieFactory.buildAccessTokenCookie(token).toString())
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        UserResponseDTO response = userMapper.toResponseDTO(authService.register(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, cookieFactory.buildExpiredAccessTokenCookie().toString());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(userMapper.toResponseDTO(user));
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<Void> completeRegistration(
            @CookieValue("pending_registration") String pendingToken,
            @RequestBody @Valid OAuthCompleteRegistrationDTO dto,
            HttpServletResponse response
            ) {

        User user = authService.completeOAuthRegistration(pendingToken, dto);
        String jwt = jwtService.generateToken(user);

        response.addHeader(HttpHeaders.SET_COOKIE, cookieFactory.buildAccessTokenCookie(jwt).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieFactory.buildExpiredPendingRegistrationCookie().toString());
        return ResponseEntity.ok().build();
    }
}
