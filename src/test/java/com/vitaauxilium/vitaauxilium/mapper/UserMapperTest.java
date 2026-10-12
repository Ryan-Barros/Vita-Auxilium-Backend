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

/**
 * Testa a implementação REAL gerada pelo MapStruct (via Mappers.getMapper),
 * não um mock. Como UserMapper usa unmappedTargetPolicy = ReportingPolicy.ERROR,
 * o simples fato de o projeto compilar já garante que todo campo de destino tem
 * mapeamento ou @Mapping(ignore=true) explícito — estes testes validam o
 * COMPORTAMENTO desses mapeamentos, não apenas a existência deles.
 */
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
        assertEquals("senha123", entidade.getPassword()); // hashing acontece em AuthService, não no mapper

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
        // UserResponseDTO nem sequer tem campo password/cpf — a garantia é estrutural,
        // reforçada aqui para documentar a intenção de design.
    }

    @Test
    @DisplayName("updateEntityFromDTO deve ignorar campos nulos do DTO e preservar os valores existentes na entidade")
    void updateEntityFromDTODeveIgnorarCamposNulos() {
        User entidadeExistente = new User();
        entidadeExistente.setName("Nome Original");
        entidadeExistente.setPhone("11911111111");
        entidadeExistente.setEmail("original@vitaauxilium.com");

        // Apenas o nome é enviado no PATCH; telefone e e-mail continuam nulos na origem
        User patch = new User();
        patch.setName("Nome Atualizado");

        userMapper.updateEntityFromDTO(patch, entidadeExistente);

        assertEquals("Nome Atualizado", entidadeExistente.getName());
        assertEquals("11911111111", entidadeExistente.getPhone(), "Campo não enviado no PATCH deve permanecer");
        assertEquals("original@vitaauxilium.com", entidadeExistente.getEmail(), "Campo não enviado no PATCH deve permanecer");
    }

    @Test
    @DisplayName("toEntityFromUpdateDTO deve ignorar 'profile' (perfil não é alterável via PATCH de usuário)")
    void toEntityFromUpdateDTODeveIgnorarProfile() {
        UserUpdateDTO dto = new UserUpdateDTO("Nome", "11999999999", "email@vitaauxilium.com", "novaSenha");

        User entidade = userMapper.toEntityFromUpdateDTO(dto);

        assertNull(entidade.getProfile());
    }
}
