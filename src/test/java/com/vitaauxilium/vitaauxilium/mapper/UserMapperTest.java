package com.vitaauxilium.vitaauxilium.mapper;

import com.vitaauxilium.vitaauxilium.dto.request.UserRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.UserUpdateDTO;
import com.vitaauxilium.vitaauxilium.dto.response.UserResponseDTO;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("toEntity deve ignorar campos sensíveis/derivados (id, picture, cpf, active, activeAmbient)")
    void toEntityDeveIgnorarCamposSensiveis() {
        UserRequestDTO dto = new UserRequestDTO(
                "Maria Helena", "11988887777", Profile.ELDERLY, "maria@vitaauxilium.com", "senha123");

        User entidade = userMapper.toEntity(dto);

        assertEquals("Maria Helena", entidade.getName());
        assertEquals("11988887777", entidade.getPhone());
        assertEquals(Profile.ELDERLY, entidade.getProfile());
        assertEquals("maria@vitaauxilium.com", entidade.getEmail());
        assertEquals("senha123", entidade.getPassword());

        assertNull(entidade.getId());
        assertNull(entidade.getPicture());
        assertNull(entidade.getCpfHash());
        assertTrue(entidade.isActive());
        assertNull(entidade.getActiveAmbient());
        assertTrue(entidade.getEnvironments().isEmpty());
    }

    @Test
    @DisplayName("toResponseDTO não deve expor senha nem CPF (contrato de exposição segura)")
    void toResponseDTONaoDeveExporCamposSensiveis() {
        User user = new User();
        user.setName("Ana Cuidadora");
        user.setPhone("11977776666");
        user.setEmail("ana@vitaauxilium.com");
        user.setProfile(Profile.CAREGIVER);
        user.setPassword("hash-nao-deveria-vazar");
        user.setCpfHash("12345678900");

        UserResponseDTO dto = userMapper.toResponseDTO(user);

        assertEquals("Ana Cuidadora", dto.name());
        assertEquals("CAREGIVER", dto.profile());
    }

    @Test
    @DisplayName("updateFromDTO deve ignorar campos nulos do DTO e preservar os valores existentes na entidade")
    void updateFromDTODeveIgnorarCamposNulos() {
        User entidadeExistente = new User();
        entidadeExistente.setName("Nome Original");
        entidadeExistente.setPhone("11911111111");
        entidadeExistente.setEmail("original@vitaauxilium.com");
        entidadeExistente.setProfile(Profile.FAMILY);
        entidadeExistente.setPassword("hashOriginal");

        UserUpdateDTO patch = new UserUpdateDTO("Nome Atualizado", null, null);

        userMapper.updateFromDTO(patch, entidadeExistente);

        assertEquals("Nome Atualizado", entidadeExistente.getName());
        assertEquals("11911111111", entidadeExistente.getPhone());
        assertEquals("original@vitaauxilium.com", entidadeExistente.getEmail());
        assertEquals(Profile.FAMILY, entidadeExistente.getProfile());
        assertEquals("hashOriginal", entidadeExistente.getPassword());
    }
}
