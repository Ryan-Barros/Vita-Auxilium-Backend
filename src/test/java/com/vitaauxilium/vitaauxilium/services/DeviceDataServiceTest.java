package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.mapper.DeviceDataMapper;
import com.vitaauxilium.vitaauxilium.models.DeviceData;
import com.vitaauxilium.vitaauxilium.repositories.DeviceDataRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceDataServiceTest {

    @Mock
    private DeviceDataRepository deviceDataRepository;

    private final DeviceDataMapper deviceDataMapper = Mappers.getMapper(DeviceDataMapper.class);

    private DeviceDataService deviceDataService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        deviceDataService = new DeviceDataService(deviceDataRepository, deviceDataMapper);
    }

    @Test
    @DisplayName("findAll deve mapear a lista de entidades para DTOs de resposta")
    void findAllDeveMapearParaDTOs() {
        DeviceData dado = new DeviceData();
        dado.setBpm(72);
        when(deviceDataRepository.findAll()).thenReturn(List.of(dado));

        var resultado = deviceDataService.findAll();

        assertEquals(1, resultado.size());
        assertEquals(72, resultado.get(0).bpm());
    }

    @Test
    @DisplayName("findById deve lançar EntityNotFoundException quando não encontrado")
    void findByIdDeveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(deviceDataRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> deviceDataService.findById(id));
    }

    @Test
    @DisplayName("create deve delegar diretamente ao repositório")
    void createDeveDelegarAoRepositorio() {
        DeviceData dado = new DeviceData();
        when(deviceDataRepository.save(dado)).thenReturn(dado);

        DeviceData resultado = deviceDataService.create(dado);

        assertSame(dado, resultado);
    }

    @Test
    @DisplayName("delete deve lançar EntityNotFoundException quando o registro não existir")
    void deleteDeveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(deviceDataRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> deviceDataService.delete(id));
        verify(deviceDataRepository, never()).delete(any());
    }
}
