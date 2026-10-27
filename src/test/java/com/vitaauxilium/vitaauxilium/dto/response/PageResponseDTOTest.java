package com.vitaauxilium.vitaauxilium.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseDTOTest {

    @Test
    @DisplayName("from deve mapear corretamente Page de DTO para PageResponseDTO")
    void fromDeveMapearPageDireta() {
        List<String> items = List.of("A", "B", "C");
        Page<String> page = new PageImpl<>(items, PageRequest.of(0, 10), 3);

        PageResponseDTO<String> response = PageResponseDTO.from(page);

        assertEquals(3, response.content().size());
        assertEquals(0, response.page());
        assertEquals(10, response.size());
        assertEquals(3L, response.totalElements());
        assertEquals(1, response.totalPages());
        assertTrue(response.first());
        assertTrue(response.last());
    }

    @Test
    @DisplayName("from com mapper deve transformar entidades em DTOs")
    void fromDeveTransformarEntidadesComMapper() {
        record SampleEntity(String name) {}
        record SampleDTO(String uppercaseName) {}

        List<SampleEntity> entities = List.of(new SampleEntity("alpha"), new SampleEntity("beta"));
        Page<SampleEntity> page = new PageImpl<>(entities, PageRequest.of(1, 2), 5);

        PageResponseDTO<SampleDTO> response = PageResponseDTO.from(page, e -> new SampleDTO(e.name().toUpperCase()));

        assertEquals(2, response.content().size());
        assertEquals("ALPHA", response.content().get(0).uppercaseName());
        assertEquals("BETA", response.content().get(1).uppercaseName());
        assertEquals(1, response.page());
        assertEquals(2, response.size());
        assertEquals(5L, response.totalElements());
        assertEquals(3, response.totalPages());
        assertFalse(response.first());
        assertFalse(response.last());
    }
}
