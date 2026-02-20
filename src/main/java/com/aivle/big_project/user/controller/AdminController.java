package com.aivle.big_project.user.controller;

import com.aivle.big_project.security.SecurityUtils;
import com.aivle.big_project.user.dto.user.UpdateUserRoleRequest;
import com.aivle.big_project.user.dto.user.UserListResponse;
import com.aivle.big_project.user.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // 사용자 전체 목록 조회
    @GetMapping
    public ResponseEntity<?> allUsers(
            @RequestParam(required = false, name = "keyword") String keyword,
            @RequestParam(required = false, name = "department") String department,
            @RequestParam(required = false, name = "rank") String rankName
    ) {
        UUID adminId = SecurityUtils.currentUserId();

        UserListResponse res = adminService.listUsers(
                adminId, keyword, department, rankName
        );
        return ResponseEntity.ok(res);
    }

    // 사용자 상세 조회
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetail(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminService.getUserDetail(userId));
    }

    // 사용자 권한 변경
    @PatchMapping("/{userId}")
    public ResponseEntity<?> updateUserRole(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRoleRequest req
    ) {
        return ResponseEntity.ok(adminService.changeRole(userId, req.getRole()));
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
    }
}
