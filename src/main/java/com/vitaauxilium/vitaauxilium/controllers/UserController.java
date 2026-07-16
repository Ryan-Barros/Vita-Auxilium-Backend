package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.UserUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.UserResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userMapper.toResponseDTOList(userService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(userMapper.toResponseDTO(userService.findById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@PathVariable UUID id, @RequestBody @Valid UserUpdateDTO dto) {
        User changes = userMapper.toEntityFromUpdateDTO(dto);
        User updated = userService.update(id, changes);
        return ResponseEntity.accepted().body(userMapper.toResponseDTO(updated));
    }

}
