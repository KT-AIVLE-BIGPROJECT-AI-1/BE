package com.aivle.big_project.user.service;

import com.aivle.big_project.security.service.AuthTokenService;
import com.aivle.big_project.security.store.RefreshTokenStore;
import com.aivle.big_project.storage.ObjectStorageService;
import com.aivle.big_project.user.dto.auth.*;
import com.aivle.big_project.user.dto.user.UserDetailResponse;
import com.aivle.big_project.user.entity.Role;
import com.aivle.big_project.user.entity.User;
import com.aivle.big_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final ObjectStorageService objectStorageService;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final RefreshTokenStore refreshTokenStore;

    // 회원가입
    @Transactional
    public UserDetailResponse signup(SignupRequest req) {
        if(userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.INVESTIGATOR)
                .phoneNumber(req.getPhoneNumber())
                .profileImg(null)
                .jurisdictionStation(req.getJurisdictionStation())
                .department(req.getDepartment())
                .rankName(req.getRankName())
                .build();

        User saved = userRepository.save(user);

        String uploadedPath = null;
        MultipartFile profileImg = req.getProfileImg();
        if(profileImg != null && !profileImg.isEmpty()) {
            uploadedPath = objectStorageService.upload(profileImg, "users/profile/" + saved.getUserId());
            saved.changeProfileImg(uploadedPath);
        }
        userRepository.save(saved);

        return toDetail(saved);
    }

    private UserDetailResponse toDetail(User u) {
        return UserDetailResponse.builder()
                .userId(u.getUserId())
                .email(u.getEmail())
                .name(u.getName())
                .phoneNumber(u.getPhoneNumber())
                .jurisdictionStation(u.getJurisdictionStation())
                .department(u.getDepartment())
                .rankName(u.getRankName())
                .role(u.getRole())
                .profileImg(u.getProfileImg())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }


    // 로그인
    @Transactional
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow();

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("올바르지 않은 이메일");
        }

        AuthTokenService.Tokens tokens =
                authTokenService.issueTokens(
                        user.getUserId(), user.getRole().name()
                );

        return toAuthTokensResponse(tokens, user.getRole());
    }

    // 로그아웃
    @Transactional
    public void logout(LogoutRequest req, UUID userId) {

        boolean match = refreshTokenStore.matches(userId, req.getRefreshToken());

        if(!match) { throw new RuntimeException("유효하지 않은 Refresh Token"); }

        authTokenService.invalidateRefreshToken(userId);
    }


    // 토큰 재발급
    @Transactional
    public TokenResponse refresh(TokenRefreshRequest req) {
        // refresh token 자체 검증
        AuthTokenService.Tokens newTokens = authTokenService.refreshTokens(req.getRefreshToken());

        return toAuthTokensResponse(newTokens, null);
    }

    private TokenResponse toAuthTokensResponse(AuthTokenService.Tokens tokens, Role role) {
        return TokenResponse.builder()
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .role(role)
                .expiresIn(tokens.expiresInSeconds())
                .build();
    }

    // 회원가입
    @Transactional
    public UserDetailResponse signup_admin(SignupRequest req) {
        if(userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일");
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.ADMIN)
                .phoneNumber(req.getPhoneNumber())
                .profileImg(null)
                .jurisdictionStation(req.getJurisdictionStation())
                .department(req.getDepartment())
                .rankName(req.getRankName())
                .build();

        User saved = userRepository.save(user);

        String uploadedPath = null;
        MultipartFile profileImg = req.getProfileImg();
        if(profileImg != null && !profileImg.isEmpty()) {
            uploadedPath = objectStorageService.upload(profileImg, "users/profile/" + saved.getUserId());
            saved.changeProfileImg(uploadedPath);
        }
        userRepository.save(saved);

        return toDetail(saved);
    }
}
