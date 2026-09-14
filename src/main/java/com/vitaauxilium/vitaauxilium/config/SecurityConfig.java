package com.vitaauxilium.vitaauxilium.config;

import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.Provider;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.models.UserOauth;
import com.vitaauxilium.vitaauxilium.security.DeviceTokenFilter;
import com.vitaauxilium.vitaauxilium.security.JwtAuthFilter;
import com.vitaauxilium.vitaauxilium.security.JwtService;
import com.vitaauxilium.vitaauxilium.security.PendingRegistrationService;
import com.vitaauxilium.vitaauxilium.services.UserOauthService;
import com.vitaauxilium.vitaauxilium.services.UserService;
import com.vitaauxilium.vitaauxilium.utils.CookieFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.Optional;

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
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    private final PendingRegistrationService pendingRegistrationService;
    private final CookieFactory cookieFactory;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/auth/login", "/auth/register", "/auth/complete-registration", "/device/data"))
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // rotas públicas (login e cadastro)
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/device/data").authenticated() // rotas do dispositivo
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

                            Optional<User> existingUser = userService.findByEmailOptional(email);

                            if (existingUser.isPresent()) {

                                User user = userService.findByEmailOptional(email)
                                        .orElseGet(() -> {
                                            User newUser = new User();
                                            newUser.setEmail(email);
                                            newUser.setName(name);
                                            newUser.setPicture(pictureUrl);
                                            newUser.setProfile(Profile.FAMILY);
                                            newUser.setPhone("");
                                            return userService.save(newUser);
                                        });

                                UserOauth oauthAccont = userOauthService
                                        .findByUserIdAndProviderOptional(user.getId(), Provider.GOOGLE)
                                        .orElseGet(() -> {
                                            UserOauth newOauth = new UserOauth();
                                            newOauth.setOauthUser(user);
                                            newOauth.setOauthSub(sub);
                                            newOauth.setOauthProvider(Provider.GOOGLE);
                                            return userOauthService.createOauthAccount(newOauth);
                                        });

                                String jwt = jwtService.generateToken(existingUser.get());
                                response.addHeader(HttpHeaders.SET_COOKIE, cookieFactory.buildAccessTokenCookie(jwt).toString());
                                response.sendRedirect("http://localhost:5173/oauth/callback");
                            } else {
                                String pendingToken = pendingRegistrationService.generateToken(email, sub, name, pictureUrl);
                                response.addHeader(HttpHeaders.SET_COOKIE, cookieFactory.buildPendingRegistrationCookie(pendingToken).toString());
                                response.sendRedirect("http://localhost:5173/auth/complete-profile");
                            }
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
