package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.HumorRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.HumorResponseDTO;
import com.vitaauxilium.vitaauxilium.dto.response.PageResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.HumorMapper;
import com.vitaauxilium.vitaauxilium.models.Humor;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.HumorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/humor")
@RequiredArgsConstructor
public class HumorController {

    private final HumorService humorService;
    private final HumorMapper humorMapper;

    @GetMapping
    public ResponseEntity<PageResponseDTO<HumorResponseDTO>> findAll(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "registrationTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Humor> page = humorService.findAll(user, pageable);
        return ResponseEntity.ok(PageResponseDTO.from(page, humorMapper::toResponseDTO));
    }

    @GetMapping("/latest")
    public ResponseEntity<HumorResponseDTO> findLatest(@AuthenticationPrincipal User user) {
        return humorService.findLatest(user)
                .map(h -> ResponseEntity.ok(humorMapper.toResponseDTO(h)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public ResponseEntity<HumorResponseDTO> create(@AuthenticationPrincipal User user,
                                                   @RequestBody @Valid HumorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(humorMapper.toResponseDTO(humorService.create(dto, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user,
                                       @PathVariable UUID id) {
        humorService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
