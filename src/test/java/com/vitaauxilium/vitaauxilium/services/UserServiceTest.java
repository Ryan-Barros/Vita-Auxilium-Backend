package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.UserUpdateDTO;
import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("findById deve retornar o usuário quando existir")
    void findByIdDeveRetornarUsuarioExistente() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User resultado = userService.findById(id);

        assertEquals(id, resultado.getId());
    }

    @Test
    @DisplayName("findById deve lançar EntityNotFoundException quando o usuário não existir")
    void findByIdDeveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    @DisplayName("findByName deve lançar EntityNotFoundException quando a busca não retornar resultados")
    void findByNameDeveLancarExcecaoQuandoListaVazia() {
        when(userRepository.findByName(anyString())).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> userService.findByName("Maria"));
    }

    @Test
    @DisplayName("findByName deve delegar ao repositório com o nome envolto em curingas '%'")
    void findByNameDeveUsarCuringasNaBusca() {
        User user = new User();
        user.setName("Maria Helena");
        when(userRepository.findByName("%Maria%")).thenReturn(List.of(user));

        List<User> resultado = userService.findByName("Maria");

        assertEquals(1, resultado.size());
        verify(userRepository).findByName("%Maria%");
    }

    @Test
    @DisplayName("update deve aplicar as alterações de UserUpdateDTO preservando perfil, e-mail e status ativo")
    void updateDeveAplicarAlteracoesPreservandoCamposProtegidos() {
        UUID id = UUID.randomUUID();

        User usuarioExistente = new User();
        usuarioExistente.setId(id);
        usuarioExistente.setName("Nome Antigo");
        usuarioExistente.setPhone("11911111111");
        usuarioExistente.setEmail("antigo@vitaauxilium.com");
        usuarioExistente.setProfile(Profile.FAMILY);
        usuarioExistente.setActive(true);

        UserUpdateDTO dto = new UserUpdateDTO("Nome Novo", "11922222222", "novaSenha123");

        when(userRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("$2a$novaHash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User resultado = userService.update(id, dto);

        assertEquals("Nome Novo", resultado.getName());
        assertEquals("11922222222", resultado.getPhone());
        assertEquals("$2a$novaHash", resultado.getPassword());
        assertEquals("antigo@vitaauxilium.com", resultado.getEmail());
        assertEquals(Profile.FAMILY, resultado.getProfile());
        assertTrue(resultado.isActive());
    }

    @Test
    @DisplayName("save deve delegar diretamente ao repositório")
    void saveDeveDelegarAoRepositorio() {
        User user = new User();
        when(userRepository.save(user)).thenReturn(user);

        User resultado = userService.save(user);

        assertSame(user, resultado);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("findByEmailOptional não deve lançar exceção quando o e-mail não existir")
    void findByEmailOptionalDeveRetornarOptionalVazio() {
        when(userRepository.findByEmail("naoexiste@vitaauxilium.com")).thenReturn(Optional.empty());

        Optional<User> resultado = userService.findByEmailOptional("naoexiste@vitaauxilium.com");

        assertTrue(resultado.isEmpty());
    }
}
