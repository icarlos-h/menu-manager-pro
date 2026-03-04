package com.menumgr.menumanagerprobackend.service;

import com.menumgr.menumanagerprobackend.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : null;
    }

    public Long requireUnitId(User user) {
        if (user.getUnitId() == null) {
            throw new IllegalArgumentException("Usuário não possui unidade vinculada.");
        }
        return user.getUnitId();
    }
}