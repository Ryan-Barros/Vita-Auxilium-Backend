package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.ActivityRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.ActivityStatusUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.request.ActivityUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.ActivityResponseDTO;
import com.vitaauxilium.vitaauxilium.dto.response.PageResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.ActivityMapper;
import com.vitaauxilium.vitaauxilium.models.Activity;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.ActivityService;
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
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityMapper activityMapper;

    @GetMapping
    public ResponseEntity<PageResponseDTO<ActivityResponseDTO>> findAll(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "activityStart", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Activity> page = activityService.findAll(user, pageable);
        return ResponseEntity.ok(PageResponseDTO.from(page, activityMapper::toResponseDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> findById(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        return ResponseEntity.ok(activityMapper.toResponseDTO(activityService.findById(id, user)));
    }

    @PostMapping
    public ResponseEntity<ActivityResponseDTO> save(@AuthenticationPrincipal User user,
                                                    @RequestBody @Valid ActivityRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityMapper.toResponseDTO(activityService.create(dto, user)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> update(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid ActivityUpdateDTO dto,
                                                      @PathVariable UUID id) {
        return ResponseEntity.ok(activityMapper.toResponseDTO(activityService.update(id, dto, user)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ActivityResponseDTO> updateStatus(@AuthenticationPrincipal User user,
                                                            @RequestBody @Valid ActivityStatusUpdateDTO dto,
                                                            @PathVariable UUID id) {
        return ResponseEntity.ok(activityMapper.toResponseDTO(
                activityService.updateStatus(id, dto.activityStatus(), user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        activityService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
