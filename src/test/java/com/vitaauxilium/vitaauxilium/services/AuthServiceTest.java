package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.dto.request.AuthRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.OAuthCompleteRegistrationDTO;
import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.UserOauthRepository;
import com.vitaauxilium.vitaauxilium.repositories.UserRepository;
import com.vitaauxilium.vitaauxilium.security.JwtService;
import com.vitaauxilium.vitaauxilium.security.PendingRegistrationService;
import com.vitaauxilium.vitaauxilium.utils.EmailService;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;
    @Mock
    private PendingRegistrationService pendingRegistrationService;
    @Mock
    private UserOauthRepository userOauthRepository;

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // @InjectMocks usa reflection e injeta os @Mock; como userMapper não é
        // um @Mock e sim uma instância real do MapStruct, setamos manualmente.
        authService = new AuthService(authenticationManager, jwtService, userRepository,
                userMapper, passwordEncoder, emailService, pendingRegistrationService, userOauthRepository);
    }

    @Test
    @DisplayName("login deve autenticar via AuthenticationManager e gerar um JWT para o usuário encontrado")
    void loginDeveAutenticarEGerarToken() {
        AuthRequestDTO dto = new AuthRequestDTO("familia@vitaauxilium.com", "senha123");
        User user = new User();
        user.setEmail(dto.email());
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-gerado");

        String token = authService.login(dto);

        assertEquals("jwt-gerado", token);
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("login deve lançar EntityNotFoundException se o usuário autenticado não for encontrado no banco")
    void loginDeveLancarExcecaoSeUsuarioNaoEncontradoAposAutenticar() {
        AuthRequestDTO dto = new AuthRequestDTO("fantasma@vitaauxilium.com", "senha123");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> authService.login(dto));
    }

    @Test
    @DisplayName("register deve lançar IllegalArgumentException quando o e-mail já estiver cadastrado")
    void registerDeveLancarExcecaoParaEmailDuplicado() {
        User novoUsuario = new User();
        novoUsuario.setEmail("duplicado@vitaauxilium.com");
        when(userRepository.findByEmail("duplicado@vitaauxilium.com")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(novoUsuario));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register deve codificar a senha com o PasswordEncoder antes de salvar")
    void registerDeveCodificarSenha() {
        User novoUsuario = new User();
        novoUsuario.setEmail("novo@vitaauxilium.com");
        novoUsuario.setName("Novo");
        novoUsuario.setPassword("senha-em-claro");

        when(userRepository.findByEmail("novo@vitaauxilium.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha-em-claro")).thenReturn("$2a$hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User salvo = authService.register(novoUsuario);

        assertEquals("$2a$hash", salvo.getPassword());
    }

    @Test
    @DisplayName("register deve concluir com sucesso mesmo se o envio de e-mail de boas-vindas falhar")
    void registerNaoDevePropagaFalhaDeEnvioDeEmail() {
        User novoUsuario = new User();
        novoUsuario.setEmail("novo2@vitaauxilium.com");
        novoUsuario.setName("Novo Dois");
        novoUsuario.setPassword("senha");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("SMTP indisponível"))
                .when(emailService).enviarEmailTexto(anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> authService.register(novoUsuario));
    }

    @Test
    @DisplayName("completeOAuthRegistration deve montar o User a partir dos claims do token pendente + perfil/telefone do DTO")
    void completeOAuthRegistrationDeveMontarUsuarioComDadosDoTokenEDto() {
        Claims claimsFake = mock(Claims.class);
        when(claimsFake.getSubject()).thenReturn("google@vitaauxilium.com");
        when(claimsFake.get("name", String.class)).thenReturn("Usuário Google");
        when(claimsFake.get("picture", String.class)).thenReturn("http://foto.png");
        when(claimsFake.get("oauth_sub", String.class)).thenReturn("sub-google-999");

        when(pendingRegistrationService.parseAndValidate("token-pendente")).thenReturn(claimsFake);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        OAuthCompleteRegistrationDTO dto = new OAuthCompleteRegistrationDTO(Profile.ELDERLY, "11988887777");

        User resultado = authService.completeOAuthRegistration("token-pendente", dto);

        assertEquals("google@vitaauxilium.com", resultado.getEmail());
        assertEquals("Usuário Google", resultado.getName());
        assertEquals(Profile.ELDERLY, resultado.getProfile());
        assertEquals("11988887777", resultado.getPhone());
        verify(userOauthRepository).save(any());
    }
}
