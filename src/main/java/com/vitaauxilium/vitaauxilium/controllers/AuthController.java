package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.AuthRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.UserRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.AuthResponseDTO;
import com.vitaauxilium.vitaauxilium.dto.response.UserResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid AuthRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        UserResponseDTO response = userMapper.toResponseDTO(authService.register(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
