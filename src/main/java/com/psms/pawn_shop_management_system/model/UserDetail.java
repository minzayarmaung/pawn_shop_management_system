package com.psms.pawn_shop_management_system.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
public class UserDetail implements UserDetails {

    private User user;

    public UserDetail(Optional<User> user) {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole())
        );
    }
    public String getEmail() {
        return user.getEmail(); // assuming your User entity has getEmail()
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // map to your User entity’s password
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // or user.getEmail(), depending on your login logic
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // customize if you have account expiry
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // customize if you have account locking
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // customize if credentials expire
    }

    @Override
    public boolean isEnabled() {
        return true; // or user.isEnabled() if you have that field
    }
}
