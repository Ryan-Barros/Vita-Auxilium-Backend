package com.vitaauxilium.vitaauxilium.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitaauxilium.vitaauxilium.dto.request.AuthRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.request.UserRequestDTO;
import com.vitaauxilium.vitaauxilium.dto.response.UserResponseDTO;
import com.vitaauxilium.vitaauxilium.mapper.UserMapper;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.DeviceRepository;
import com.vitaauxilium.vitaauxilium.security.DeviceRateLimiterService;
import com.vitaauxilium.vitaauxilium.security.JwtService;
import com.vitaauxilium.vitaauxilium.security.PendingRegistrationService;
import com.vitaauxilium.vitaauxilium.services.AuthService;
import com.vitaauxilium.vitaauxilium.services.UserOauthService;
import com.vitaauxilium.vitaauxilium.services.UserService;
import com.vitaauxilium.vitaauxilium.utils.CookieFactory;
import com.vitaauxilium.vitaauxilium.utils.DeviceCrypto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Padrão de teste de controller: @WebMvcTest restrito ao controller sob teste,
 * com addFilters=false para NÃO exercitar JwtAuthFilter/DeviceTokenFilter/CSRF
 * aqui (isso é responsabilidade de um teste de integração @SpringBootTest,
 * documentado como próximo passo no README). O objetivo aqui é validar apenas
 * o contrato HTTP do controller: status, corpo e cabeçalhos (ex.: Set-Cookie).
 * <p>
 * Dependência necessária no pom.xml (escopo test), caso ainda não exista:
 * org.springframework.security:spring-security-test
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private CookieFactory cookieFactory;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private DeviceRepository deviceRepository;

    @MockitoBean
    private DeviceCrypto deviceCrypto;

    @MockitoBean
    private DeviceRateLimiterService deviceRateLimiterService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserOauthService userOauthService;

    @MockitoBean
    private PendingRegistrationService pendingRegistrationService;

    @Test
    @DisplayName("POST /auth/login com credenciais válidas deve retornar 200 e o cookie access_token")
    void loginComSucessoDeveRetornar200ESetCookie() throws Exception {
        AuthRequestDTO dto = new AuthRequestDTO("familia@vitaauxilium.com", "senha123");
        when(authService.login(dto)).thenReturn("jwt-fake");
        when(cookieFactory.buildAccessTokenCookie("jwt-fake"))
                .thenReturn(ResponseCookie.from("access_token", "jwt-fake").httpOnly(true).build());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("access_token=jwt-fake")));
    }

    @Test
    @DisplayName("POST /auth/login com corpo inválido (e-mail em branco) deve retornar 400 por causa de @Valid")
    void loginComCorpoInvalidoDeveRetornar400() throws Exception {
        String jsonInvalido = "{\"email\": \"\", \"password\": \"123\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/register com dados válidos deve retornar 201 com o UserResponseDTO")
    void registerComSucessoDeveRetornar201() throws Exception {
        UserRequestDTO dto = new UserRequestDTO(
                "Maria Helena", "11988887777", Profile.ELDERLY, "maria@vitaauxilium.com", "senha123");
        User entidadeMapeada = new User();
        UserResponseDTO resposta = new UserResponseDTO("Maria Helena", "11988887777", "maria@vitaauxilium.com", "ELDERLY");

        when(userMapper.toEntity(dto)).thenReturn(entidadeMapeada);
        when(authService.register(entidadeMapeada)).thenReturn(entidadeMapeada);
        when(userMapper.toResponseDTO(entidadeMapeada)).thenReturn(resposta);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Maria Helena"))
                .andExpect(jsonPath("$.profile").value("ELDERLY"));
    }

    @Test
    @DisplayName("GET /auth/me sem usuário autenticado deve retornar 401")
    void meSemUsuarioAutenticadoDeveRetornar401() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /auth/me com usuário autenticado deve retornar os dados do usuário")
    void meComUsuarioAutenticadoDeveRetornarDados() throws Exception {
        User usuarioLogado = new User();
        usuarioLogado.setEmail("cuidador@vitaauxilium.com");
        usuarioLogado.setProfile(Profile.CAREGIVER);
        usuarioLogado.setActive(true);

        UserResponseDTO resposta = new UserResponseDTO("Ana", "11977776666", "cuidador@vitaauxilium.com", "CAREGIVER");
        when(userMapper.toResponseDTO(usuarioLogado)).thenReturn(resposta);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(usuarioLogado, null, usuarioLogado.getAuthorities()));
        try {
            mockMvc.perform(get("/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("cuidador@vitaauxilium.com"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    @DisplayName("POST /auth/logout deve retornar 200 e o cookie access_token expirado")
    void logoutDeveRetornar200EExpirarCookie() throws Exception {
        when(cookieFactory.buildExpiredAccessTokenCookie())
                .thenReturn(ResponseCookie.from("access_token", "").maxAge(0).build());

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Max-Age=0")));
    }
}
