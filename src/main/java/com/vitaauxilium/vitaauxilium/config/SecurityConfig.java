package com.vitaauxilium.vitaauxilium.config;

import com.vitaauxilium.vitaauxilium.models.Provider;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.models.UserOauth;
import com.vitaauxilium.vitaauxilium.security.DeviceTokenFilter;
import com.vitaauxilium.vitaauxilium.security.JwtAuthFilter;
import com.vitaauxilium.vitaauxilium.security.JwtService;
import com.vitaauxilium.vitaauxilium.services.UserOauthService;
import com.vitaauxilium.vitaauxilium.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final UserOauthService userOauthService;
    private final DeviceTokenFilter deviceTokenFilter;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // rotas públicas (login e cadastro)
                        .requestMatchers("/device/**").authenticated() // rotas do dispositivo
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {
                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

                            assert oauthUser != null;

                            String sub = oauthUser.getAttribute("sub");
                            String email = oauthUser.getAttribute("email");
                            String name = oauthUser.getAttribute("name");
                            String pictureUrl = oauthUser.getAttribute("picture");

                            User user = userService.findByEmail(email);

                            if (user == null) {
                                user = new User();
                                user.setEmail(email);
                                user.setName(name);
                                user.setPicture(pictureUrl);
                                userService.save(user);
                            }

                            UserOauth oauthAccont = userOauthService
                                    .findByUserIdAndProvider(user.getId(), Provider.GOOGLE);

                            if (oauthAccont == null) {
                                oauthAccont = new UserOauth();
                                oauthAccont.setOauthUser(user);
                                oauthAccont.setOauthProvider(Provider.GOOGLE);
                                userOauthService.createOauthAccount(oauthAccont);
                            }

                            String jwt = jwtService.generateToken(user);
                            response.sendRedirect("http://localhost:5173/oauth/callback?token=" + jwt);
                        }))
                .addFilterBefore(deviceTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
