package com.aivle.big_project.security.service;

import com.aivle.big_project.security.store.RefreshTokenStore;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtTokenService implements AuthTokenService {

    private final RefreshTokenStore refreshTokenStore;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration-ms:900000}")
    private long accessExpSec;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshExpSec;

    private static final String ROLE = "role";

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Tokens issueTokens(UUID userId, String role) {
        LocalDateTime now = LocalDateTime.now();

        String access = createJwt(userId, role, now, accessExpSec);
        String refresh = createJwt(userId, role, now, refreshExpSec);

        // USER.refreshTokenHash에 저장
        refreshTokenStore.save(userId, refresh);

        return new Tokens(access, refresh, accessExpSec, "Bearer");
    }

    @Override
    public Tokens refreshTokens(String rawRefreshToken) {
        Claims claims = parseClaims(rawRefreshToken);

        UUID userId = UUID.fromString(claims.getSubject());
        String role = claims.get(ROLE, String.class);

        // DB 저장된 refreshTokenHash와 일치 여부 확인
        if (!refreshTokenStore.matches(userId, rawRefreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token");
        }

        // rotation
        LocalDateTime now = LocalDateTime.now();
        String newAccess = createJwt(userId, role, now, accessExpSec);
        String newRefresh = createJwt(userId, role, now, refreshExpSec);

        refreshTokenStore.save(userId, newRefresh);

        return new Tokens(newAccess, newRefresh, accessExpSec, "Bearer");
    }

    @Override
    public void invalidateRefreshToken(UUID userId) {
        refreshTokenStore.delete(userId);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        } catch (JwtException e) {
            throw new RuntimeException("토큰이 유효하지 않습니다.");
        }
    }

    private String createJwt(UUID userId, String role, LocalDateTime now, long expSeconds) {
        LocalDateTime exp = now.plusMillis(expSeconds);

        return Jwts.builder()
                .subject(userId.toString())
                .claim(ROLE, role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }
}
