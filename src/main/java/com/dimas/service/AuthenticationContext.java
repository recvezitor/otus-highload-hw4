package com.dimas.service;

import com.dimas.domain.entity.AuthUser;

public interface AuthenticationContext {
    AuthUser getCurrentUser();

}