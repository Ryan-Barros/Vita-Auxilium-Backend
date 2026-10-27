package com.vitaauxilium.vitaauxilium.controllers;

import com.vitaauxilium.vitaauxilium.dto.response.CategoryResponseDTO;
import com.vitaauxilium.vitaauxilium.dto.response.PageResponseDTO;
import com.vitaauxilium.vitaauxilium.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<PageResponseDTO<CategoryResponseDTO>> findAll(
            @PageableDefault(size = 50, sort = "nameCategoria", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CategoryResponseDTO> page = categoryRepository.findAll(pageable)
                .map(c -> new CategoryResponseDTO(c.getId(), c.getNameCategoria()));
        return ResponseEntity.ok(PageResponseDTO.from(page));
    }
}
