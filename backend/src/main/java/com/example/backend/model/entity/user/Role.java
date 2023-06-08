package com.example.backend.model.entity.user;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN, MODERATOR, USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
