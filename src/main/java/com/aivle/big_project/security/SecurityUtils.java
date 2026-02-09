package com.aivle.big_project.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static UUID currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null || auth.getPrincipal() == null) {
            throw new RuntimeException("인증이 필요함");
        }
        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (Exception e) {
            throw new RuntimeException("올바르지 않은 인증 정보");
        }
    }

    public static UUID getCurrentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;

        try {
            return UUID.fromString(auth.getPrincipal().toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null) return false;

        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }


}
