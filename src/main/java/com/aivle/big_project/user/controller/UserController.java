package com.aivle.big_project.user.controller;

import com.aivle.big_project.security.SecurityUtils;
import com.aivle.big_project.user.dto.user.UpdateMeRequest;
import com.aivle.big_project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(userService.getMyDetail(userId));
    }

    /*
    * multipart/form-data
    * - profileImg : 파일
    * - phoneNumber, department 등 : 폼 필드
    * */
    @PatchMapping(value = "/me", consumes = "multipart/form-data")
    public ResponseEntity<?> updateMe(
            @RequestPart("data") String data,
            @RequestPart(value = "profile_img", required = false) MultipartFile profileImg
    ) {
        UpdateMeRequest req = objectMapper.readValue(data, UpdateMeRequest.class);
        req.setProfileImg(profileImg);
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(userService.updateMe(userId, req));
    }
}
