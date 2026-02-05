package com.aivle.big_project.activity_log.controller;

import com.aivle.big_project.activity_log.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.aivle.big_project.security.SecurityUtils.currentUserId;

@RestController
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    // 사건 활동 로그 목록 조회
    @GetMapping("/cases/{caseId}/activity-log")
    public ResponseEntity<?> listActivityLog(@PathVariable UUID caseId) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(activityLogService.listActivityLogs(caseId, userId));
    }
}
