package com.vitaauxilium.vitaauxilium.security;

import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Deve carregar o usuário pelo e-mail (username do Spring Security)")
    void deveCarregarUsuarioPorEmail() {
        User user = new User();
        user.setEmail("cuidador@vitaauxilium.com");
        user.setProfile(Profile.CAREGIVER);
        user.setActive(true);

        when(userRepository.findByEmail("cuidador@vitaauxilium.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("cuidador@vitaauxilium.com");

        assertEquals("cuidador@vitaauxilium.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAREGIVER")));
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o e-mail não existir")
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(userRepository.findByEmail("inexistente@vitaauxilium.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("inexistente@vitaauxilium.com"));
    }
}
