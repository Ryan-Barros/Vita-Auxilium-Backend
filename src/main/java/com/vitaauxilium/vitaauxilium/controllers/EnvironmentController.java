package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentJoinDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.EnvironmentUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.EnvironmentResponseDTO;
import com.vitaauxilium.vitaauxilium.dto.response.PageResponseDTO;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.EnvironmentMemberFacadeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public ResponseEntity<PageResponseDTO<EnvironmentResponseDTO>> findAll(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 10, sort = "environmentName", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(environmentMemberFacadeService.findAllByUser(user, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvironmentResponseDTO> findById(@AuthenticationPrincipal User user,
                                                           @PathVariable UUID id) {
        return ResponseEntity.ok(environmentMemberFacadeService.findByIdForUser(user, id));
    }

    @PostMapping
    public ResponseEntity<EnvironmentResponseDTO> createEnvironment(@AuthenticationPrincipal User user,
                                                                    @RequestBody @Valid EnvironmentRequestDTO dto) {
        EnvironmentResponseDTO response = environmentMemberFacadeService.createWithRelation(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/join")
    public ResponseEntity<EnvironmentResponseDTO> joinEnvironment(@AuthenticationPrincipal User user,
                                                                  @RequestBody @Valid EnvironmentJoinDTO dto) {
        EnvironmentResponseDTO response = environmentMemberFacadeService.joinByCode(user, dto.code());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EnvironmentResponseDTO> updateEnvironment(@AuthenticationPrincipal User user,
                                                                    @RequestBody @Valid EnvironmentUpdateDTO dto,
                                                                    @PathVariable UUID id) {
        EnvironmentResponseDTO response = environmentMemberFacadeService.updateWithRelation(user, dto, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvironment(@AuthenticationPrincipal User user,
                                                  @PathVariable UUID id) {
        environmentMemberFacadeService.deleteWithRelation(user, id);
        return ResponseEntity.noContent().build();
    }
}
