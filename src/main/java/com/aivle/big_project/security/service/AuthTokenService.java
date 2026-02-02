package com.aivle.big_project.security.service;

import java.util.UUID;

public interface AuthTokenService {
    record Tokens(String accessToken, String refreshToken, long expiresInSeconds, String tokenType) {}

    Tokens issueTokens(UUID userId, String role);

    Tokens refreshTokens(String refreshToken);

    void invalidateRefreshToken(UUID userId);
}
