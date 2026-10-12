package com.vitaauxilium.vitaauxilium.services;

import com.vitaauxilium.vitaauxilium.models.Environment;
import com.vitaauxilium.vitaauxilium.models.EnvironmentMember;
import com.vitaauxilium.vitaauxilium.models.Profile;
import com.vitaauxilium.vitaauxilium.models.User;
import com.vitaauxilium.vitaauxilium.repositories.EnvironmentRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EnvironmentRepositoryTest {

    private final TestEntityManager em;
    private final EnvironmentRepository repository;

    private User userA, userB;
    private Environment envA, envShared;

    @BeforeEach
    void setUp() {
        userA = persistUser("a@teste.com");
        userB = persistUser("b@teste.com");

        envA = persistEnv("Família A", "INVAAAAAAAA", userA);
        envShared = persistEnv("Família Compartilhada", "INVBBBBBBBB", userA, userB);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("findAllByMemberId retorna apenas ambientes dos quais o usuário é membro")
    void retornaSomenteAmbientesDoUsuario() {
        List<Environment> doUserB = repository.findAllByMemberId(userB.getId());

        assertEquals(1, doUserB.size());
        assertEquals(envShared.getId(), doUserB.get(0).getId());
    }

    @Test
    @DisplayName("findAllByMemberId não duplica linhas nem reduz a lista de members")
    void naoDuplicaENaoReduzMembers() {
        List<Environment> doUserA = repository.findAllByMemberId(userA.getId());

        assertEquals(2, doUserA.size());
        Environment compartilhado = doUserA.stream()
                .filter(e -> e.getId().equals(envShared.getId())).findFirst().orElseThrow();
        assertEquals(2, compartilhado.getMembers().size());
    }

    @Test
    @DisplayName("findByIdAndMemberId retorna vazio para quem não é membro (IDOR)")
    void naoRetornaAmbienteDeOutraFamilia() {
        assertTrue(repository.findByIdAndMemberId(envA.getId(), userB.getId()).isEmpty());
        assertTrue(repository.findByIdAndMemberId(envA.getId(), userA.getId()).isPresent());
    }

    // helpers
    private User persistUser(String email) {
        User u = new User();
        u.setProfile(Profile.FAMILY);
        u.setName("Usuário Teste");
        u.setPhone("11999999999");
        u.setEmail(email);
        return em.persist(u);
    }

    private Environment persistEnv(String name, String code, User... users) {
        Environment env = new Environment();
        env.setEnvironmentName(name);
        env.setEnvironmentCode(code);
        env.setExpirationDate(Instant.now().plus(30, ChronoUnit.DAYS));
        for (User u : users) {
            EnvironmentMember m = new EnvironmentMember();
            m.setUser(u);
            env.addMember(m);
        }
        return em.persist(env);
    }
}