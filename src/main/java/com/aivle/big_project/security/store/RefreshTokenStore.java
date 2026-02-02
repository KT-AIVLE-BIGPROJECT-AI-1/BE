package com.aivle.big_project.security.store;

import java.util.UUID;

public interface RefreshTokenStore {

    // userId의 refreshTokenHash를 새 값으로 저장
    void save(UUID userId, String rawRefreshToken);

    // userId에 저장된 refreshTokenHash가 rawRefreshToken과 일치하는지 확인
    boolean matches (UUID userId, String rawRefreshToken);

    // userId의 refreshTokenHash 제거
    void delete(UUID userId);
}
