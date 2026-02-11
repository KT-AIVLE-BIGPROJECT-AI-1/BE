package com.aivle.big_project.audit_log.controller;

import com.aivle.big_project.audit_log.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> list(
            @PathVariable UUID userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime to

    ) {
        return ResponseEntity.ok(auditLogService.search(userId, action, targetType, from, to));
    }
}
