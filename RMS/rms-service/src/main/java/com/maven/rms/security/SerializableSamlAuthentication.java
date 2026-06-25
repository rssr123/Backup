package com.maven.rms.security;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SerializableSamlAuthentication implements Authentication, Serializable {

    private static final long serialVersionUID = 1L;

    private final SerializableSamlPrincipal principal;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean authenticated;

    public SerializableSamlAuthentication(SerializableSamlPrincipal principal,
                                          Collection<? extends GrantedAuthority> authorities,
                                          boolean authenticated) {
        this.principal = principal;
        this.authorities = authorities;
        this.authenticated = authenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        // no-op or throw if you want; keep as no-op for simplicity
    }

    @Override
    public String getName() {
        return principal.getName();
    }
}
