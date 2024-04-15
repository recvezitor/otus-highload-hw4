package com.dimas.service;

import com.dimas.domain.entity.AuthUser;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AuthenticationContextImpl implements AuthenticationContext {
    private AuthUser user;

    @Override
    public AuthUser getCurrentUser() {
        return user;
    }

    public void setCurrentUser(AuthUser user) {
        this.user = user;
    }

}