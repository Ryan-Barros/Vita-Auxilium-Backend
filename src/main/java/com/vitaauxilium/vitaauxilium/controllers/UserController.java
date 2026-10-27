package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.UserUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.UserResponseDTO;
import com.vitaauxilium.vitaauxilium.exception.AccessDeniedException;
import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.EnvironmentMemberFacadeService;
import com.vitaauxilium.vitaauxilium.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final EnvironmentMemberFacadeService environmentMemberFacadeService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> findMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userMapper.toResponseDTO(userService.findById(user.getId())));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMe(@AuthenticationPrincipal User user,
                                                    @RequestBody @Valid UserUpdateDTO dto) {
        User updated = userService.update(user.getId(), dto);
        return ResponseEntity.ok(userMapper.toResponseDTO(updated));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateMe(@AuthenticationPrincipal User user) {
        userService.deactivate(user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@AuthenticationPrincipal User currentUser,
                                                    @PathVariable UUID id) {
        if (currentUser != null && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Acesso negado aos dados de outro usuário!");
        }
        return ResponseEntity.ok(userMapper.toResponseDTO(userService.findById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(@AuthenticationPrincipal User currentUser,
                                                  @PathVariable UUID id,
                                                  @RequestBody @Valid UserUpdateDTO dto) {
        if (currentUser != null && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Acesso negado para editar outro usuário!");
        }
        User updated = userService.update(id, dto);
        return ResponseEntity.ok(userMapper.toResponseDTO(updated));
    }

    @PatchMapping("/me/active-environment/{envId}")
    public ResponseEntity<Void> setActiveEnvironment(@AuthenticationPrincipal User user,
                                                     @PathVariable UUID envId) {
        environmentMemberFacadeService.setActiveEnvironment(user, envId);
        return ResponseEntity.noContent().build();
    }
}
