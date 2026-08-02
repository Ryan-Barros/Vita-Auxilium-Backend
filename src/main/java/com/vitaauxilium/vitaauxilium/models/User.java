package com.vitaauxilium.vitaauxilium.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vitaauxilium.vitaauxilium.config.GeneratedUuidV7;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.br.CPF;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Audited
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedUuidV7
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_profile", nullable = false)
    private Profile profile;

    @Column(name = "user_name", nullable = false, length = 45)
    private String name;

    @Column(name = "user_phone", nullable = false, length = 11)
    private String phone;

    @Email
    @Column(name = "user_email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "user_picture")
    private String picture;

    @CPF
    @Column(name = "cpf")
    private String cpf;

    @JsonIgnore
    @Column(name = "user_password", length = 60)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "active_ambient")
    private UUID activeAmbient;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<EnvironmentMember> environments = new ArrayList<>();

    @NullMarked
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.profile.getRoleName()));
        return authorities;
    }

    @NullMarked
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return this.active;
    }

}
