package com.aivle.big_project.user.controller;
import com.aivle.big_project.security.service.JwtTokenService;

import com.aivle.big_project.security.SecurityUtils;
import com.aivle.big_project.security.service.AuthTokenService;
import com.aivle.big_project.user.dto.auth.*;
import com.aivle.big_project.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final JwtTokenService jwtTokenService;

    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    public ResponseEntity<?> signup(
            @RequestPart("data") String data,
            @RequestPart(value = "profile_img", required = false) MultipartFile profileImg
    ) {
        if (profileImg != null) {
            System.out.println("profileImg name = " + profileImg.getOriginalFilename());
        }

        SignupRequest req = objectMapper.readValue(data, SignupRequest.class);
        req.setProfileImg(profileImg);
        return ResponseEntity.ok(authService.signup(req));
    }

    /*@PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }*/

    // 발표용 로그인
    @PostMapping("/login")
    public ResponseEntity<AuthTokenService.Tokens> login(
            @RequestBody LoginRequest req
    ) {
        return ResponseEntity.ok(
                jwtTokenService.issueTokens(
                        UUID.fromString("0f345386-3c89-4e80-a068-8caab2117360"),
                        "INVESTIGATOR"
                )
        );
    }

    // 헤더에 Access Token 필요
    // Refresh Token도 요청 Body에 받아서 DB 값과 일치하면 삭제
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody LogoutRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        authService.logout(req, userId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody TokenRefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req));
    }

    @PostMapping(value = "/signup/admin", consumes = "multipart/form-data")
    public ResponseEntity<?> signup_admin(
            @RequestPart("data") String data,
            @RequestPart(value = "profile_img", required = false) MultipartFile profileImg
    ) {
        SignupRequest req = objectMapper.readValue(data, SignupRequest.class);
        req.setProfileImg(profileImg);
        return ResponseEntity.ok(authService.signup_admin(req));
    }
}
