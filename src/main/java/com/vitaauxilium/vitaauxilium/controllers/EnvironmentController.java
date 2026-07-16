package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.EnvironmentMemberFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/environment")
@RequiredArgsConstructor
public class EnvironmentController {

    private final EnvironmentMemberFacadeService environmentMemberFacadeService;

    @PostMapping
    public ResponseEntity<EnvironmentResponseDTO> createEnvironment(@AuthenticationPrincipal User user,
                                                                    @RequestBody @Valid EnvironmentRequestDTO dto) {
        EnvironmentResponseDTO response = environmentMemberFacadeService.createWithRelation(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EnvironmentResponseDTO> updateEnvironment(@AuthenticationPrincipal User user,
                                                                    @RequestBody @Valid EnvironmentUpdateDTO dto,
                                                                    @PathVariable UUID id) {
        EnvironmentResponseDTO response = environmentMemberFacadeService.updateWithRelation(user, dto, id);
        return ResponseEntity.ok().body(response);
    }
}
