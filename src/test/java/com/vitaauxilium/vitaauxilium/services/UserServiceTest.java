package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserServiceTest usa a implementação REAL do UserMapper (gerada pelo MapStruct
 * em tempo de compilação, via Mappers.getMapper), e não um mock — isso é
 * proposital: o objetivo do teste abaixo é expor um bug de integração entre
 * UserService e UserMapper que um mock do mapper esconderia completamente.
 */
class UserServiceTest {

    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userMapper = Mappers.getMapper(UserMapper.class);
        userService = new UserService(userRepository, userMapper);
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
    @DisplayName(
            "BUG CONHECIDO (analise_seguranca.md, achado #5): update() não aplica os campos de 'changes' " +
                    "no usuário existente, porque a ordem dos argumentos passados a updateEntityFromDTO está invertida. " +
                    "Este teste documenta o comportamento ATUAL; ele deve começar a FALHAR assim que o bug for corrigido " +
                    "em UserService.update() — nesse momento, atualize também o texto da TCC sobre este achado."
    )
    void updateNaoAplicaAsAlteracoesPorCausaDaInversaoDeParametros() {
        UUID id = UUID.randomUUID();

        User usuarioExistente = new User();
        usuarioExistente.setId(id);
        usuarioExistente.setName("Nome Antigo");
        usuarioExistente.setPhone("11911111111");
        usuarioExistente.setEmail("antigo@vitaauxilium.com");
        usuarioExistente.setProfile(Profile.FAMILY);

        User alteracoesDesejadas = new User();
        alteracoesDesejadas.setName("Nome Novo");
        alteracoesDesejadas.setPhone("11922222222");

        when(userRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User resultado = userService.update(id, alteracoesDesejadas);

        // Comportamento hoje (com o bug): o nome/telefone NÃO são atualizados,
        // porque `usuarioExistente` foi usado como origem e `alteracoesDesejadas`
        // como alvo do mapeamento — exatamente o oposto do que a intenção do método sugere.
        assertEquals("Nome Antigo", resultado.getName());
        assertEquals("11911111111", resultado.getPhone());
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
