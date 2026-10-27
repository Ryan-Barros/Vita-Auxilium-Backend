package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.models.Device;
import com.vitaauxilium.vitaauxilium.models.DeviceData;
import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.repositories.DeviceDataRepository;
import com.vitaauxilium.vitaauxilium.repositories.DeviceRepository;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import com.vitaauxilium.vitaauxilium.utils.DeviceCrypto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceDataRepository deviceDataRepository;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private DeviceCrypto deviceCrypto;

    @InjectMocks
    private DeviceService deviceService;

    @Test
    @DisplayName("create deve lançar EntityNotFoundException quando o ambiente informado não existir")
    void createDeveLancarExcecaoQuandoAmbienteNaoExiste() {
        UUID envId = UUID.randomUUID();
        when(environmentRepository.findById(envId)).thenReturn(Optional.empty());

        Device device = new Device();

        assertThrows(EntityNotFoundException.class,
                () -> deviceService.create(device, envId, "token-bruto"));

        verifyNoInteractions(deviceCrypto);
    }

    @Test
    @DisplayName("create deve vincular o ambiente e persistir apenas o hash do token, nunca o token bruto")
    void createDeveHashearTokenAntesDePersistir() {
        UUID envId = UUID.randomUUID();
        Environment environment = new Environment();
        environment.setId(envId);
        when(environmentRepository.findById(envId)).thenReturn(Optional.of(environment));
        when(deviceCrypto.hashToken("token-bruto-do-relogio")).thenReturn("hash-resultante");
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        Device device = new Device();
        device.setName("Samsung Watch");

        Device resultado = deviceService.create(device, envId, "token-bruto-do-relogio");

        assertEquals(environment, resultado.getEnvironment());
        assertEquals("hash-resultante", resultado.getTokenHash());
        verify(deviceRepository).save(device);
    }

    @Test
    @DisplayName("delete deve lançar EntityNotFoundException quando o dispositivo não existir")
    void deleteDeveLancarExcecaoQuandoDispositivoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(deviceRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> deviceService.delete(id));
        verify(deviceRepository, never()).delete(any());
    }

    @Test
    @DisplayName("delete deve remover o dispositivo quando ele existir")
    void deleteDeveRemoverDispositivoExistente() {
        UUID id = UUID.randomUUID();
        Device device = new Device();
        device.setId(id);
        when(deviceRepository.findById(id)).thenReturn(Optional.of(device));

        deviceService.delete(id);

        verify(deviceRepository).delete(device);
    }

    @Test
    @DisplayName("getData deve lançar EntityNotFoundException quando o Optional vier vazio")
    void getDataDeveLancarExcecaoQuandoOptionalVazio() {
        Device device = new Device();
        when(deviceDataRepository.findAllByDevice(device)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> deviceService.getData(device));
    }

    @Test
    @DisplayName("getData deve lançar EntityNotFoundException quando a lista vier vazia (mesmo Optional presente)")
    void getDataDeveLancarExcecaoQuandoListaVazia() {
        Device device = new Device();
        when(deviceDataRepository.findAllByDevice(device)).thenReturn(Optional.of(List.of()));

        assertThrows(EntityNotFoundException.class, () -> deviceService.getData(device));
    }

    @Test
    @DisplayName("getData deve retornar a lista quando houver dados de telemetria")
    void getDataDeveRetornarListaQuandoExistemDados() {
        Device device = new Device();
        DeviceData dado = new DeviceData();
        when(deviceDataRepository.findAllByDevice(device)).thenReturn(Optional.of(List.of(dado)));

        List<DeviceData> resultado = deviceService.getData(device);

        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("findByActiveEnvironment com Pageable deve retornar página de dispositivos")
    void findByActiveEnvironmentPaginadoDeveRetornarPaginaDeDispositivos() {
        UUID envId = UUID.randomUUID();
        com.vitaauxilium.vitaauxilium.models.User user = new com.vitaauxilium.vitaauxilium.models.User();
        user.setId(UUID.randomUUID());
        user.setActiveAmbient(envId);

        Environment env = new Environment();
        env.setId(envId);

        Device d1 = new Device();
        d1.setName("Relógio Idoso 1");

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        org.springframework.data.domain.Page<Device> mockPage = new org.springframework.data.domain.PageImpl<>(
                List.of(d1), pageable, 1);

        when(environmentRepository.findByIdAndMemberId(envId, user.getId())).thenReturn(Optional.of(env));
        when(deviceRepository.findAllByEnvironment_Id(envId, pageable)).thenReturn(mockPage);

        org.springframework.data.domain.Page<Device> result = deviceService.findByActiveEnvironment(user, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Relógio Idoso 1", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("createForUser deve permitir cadastrar múltiplos dispositivos no mesmo ambiente para FAMILY")
    void createForUserDevePermitirMultiplosDispositivosNoMesmoAmbiente() {
        UUID envId = UUID.randomUUID();
        com.vitaauxilium.vitaauxilium.models.User familyUser = new com.vitaauxilium.vitaauxilium.models.User();
        familyUser.setId(UUID.randomUUID());
        familyUser.setProfile(com.vitaauxilium.vitaauxilium.models.Profile.FAMILY);
        familyUser.setActiveAmbient(envId);

        Environment env = new Environment();
        env.setId(envId);

        when(environmentRepository.findByIdAndMemberId(envId, familyUser.getId())).thenReturn(Optional.of(env));
        when(deviceCrypto.hashToken(anyString())).thenReturn("hashed-token");
        when(deviceRepository.save(any(Device.class))).thenAnswer(inv -> inv.getArgument(0));

        com.vitaauxilium.vitaauxilium.dto.request.DeviceRequestDTO dto =
                new com.vitaauxilium.vitaauxilium.dto.request.DeviceRequestDTO(envId, "Relógio 2", "TOKEN-12345");

        Device saved = deviceService.createForUser(dto, familyUser);

        assertEquals("Relógio 2", saved.getName());
        assertEquals(env, saved.getEnvironment());
        verify(deviceRepository).save(any(Device.class));
    }
}
