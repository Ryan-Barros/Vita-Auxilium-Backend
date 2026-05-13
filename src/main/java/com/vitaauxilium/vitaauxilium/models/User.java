package com.vitaauxilium.vitaauxilium.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_name", nullable = false, length = 45)
    private String name;

    @Column(name = "user_phone", nullable = false, length = 11)
    private String phone;

    @Column(name = "user_email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "user_password", length = 60)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @NullMarked
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @NullMarked
    @Override
    public String getUsername() {
        return email;
    }

}
