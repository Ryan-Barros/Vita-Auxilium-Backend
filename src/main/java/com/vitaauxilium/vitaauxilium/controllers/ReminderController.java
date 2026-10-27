package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.request.ReminderRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.ReminderUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.PageResponseDTO;
import com.vitaauxilium.vitaauxilium.dto.response.ReminderResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.ReminderMapper;
import com.vitaauxilium.vitaauxilium.models.Reminder;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.services.ReminderService;
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
@RequestMapping("/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    @GetMapping
    public ResponseEntity<PageResponseDTO<ReminderResponseDTO>> findAll(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Reminder> page = reminderService.findAll(user, pageable);
        return ResponseEntity.ok(PageResponseDTO.from(page, reminderMapper::toResponseDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderResponseDTO> findById(@AuthenticationPrincipal User user,
                                                        @PathVariable UUID id) {
        return ResponseEntity.ok(reminderMapper.toResponseDTO(reminderService.findById(id, user)));
    }

    @PostMapping
    public ResponseEntity<ReminderResponseDTO> create(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid ReminderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reminderMapper.toResponseDTO(reminderService.create(dto, user)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReminderResponseDTO> update(@AuthenticationPrincipal User user,
                                                      @RequestBody @Valid ReminderUpdateDTO dto,
                                                      @PathVariable UUID id) {
        return ResponseEntity.ok(reminderMapper.toResponseDTO(reminderService.update(id, dto, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user,
                                       @PathVariable UUID id) {
        reminderService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
