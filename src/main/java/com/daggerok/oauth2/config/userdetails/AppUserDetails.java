package com.daggerok.oauth2.config.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

/**
 * Created by mak on 4/30/16.
 */
public class AppUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;
    final static String prefix = "ROLE_";

    private Collection<? extends GrantedAuthority> authorities;
    private String password;
    private String username;

    public AppUserDetails(User user) {

        username = user.getUsername();
        password = user.getPassword();
        authorities = user.getRoles().stream()
                .map(Role::getName)
                .map(String::toUpperCase)
                .map(this::toAuthority)
                .collect(toList());
    }

    private GrantedAuthority toAuthority(String role) {
        return new SimpleGrantedAuthority(role.startsWith(prefix) ? role : prefix.concat(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
