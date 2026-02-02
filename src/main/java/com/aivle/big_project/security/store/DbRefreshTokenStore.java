package com.aivle.big_project.security.store;

import com.aivle.big_project.security.RefreshTokenHasher;
import com.aivle.big_project.user.entity.User;
import com.aivle.big_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DbRefreshTokenStore implements RefreshTokenStore{

    private final UserRepository userRepository;
    private final RefreshTokenHasher hasher;


    @Override
    @Transactional
    public void save(UUID userId, String rawRefreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        user.updateRefreshTokenHash(hasher.sha256(rawRefreshToken));
    }

    @Override
    @Transactional
    public boolean matches(UUID userId, String rawRefreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow();

        if(user.getRefreshTokenHash() == null) return false;

        return user.getRefreshTokenHash().equals(hasher.sha256(rawRefreshToken));
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow();
        user.clearRefreshTokenHash();
    }
}
