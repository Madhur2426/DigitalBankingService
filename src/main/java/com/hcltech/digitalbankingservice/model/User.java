package com.hcltech.digitalbankingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role; // Add any other fields you need

    // Getters and setters for username, password, and role

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement as per your business logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement as per your business logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement as per your business logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement as per your business logic
    }

}