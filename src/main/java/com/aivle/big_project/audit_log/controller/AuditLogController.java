package com.aivle.big_project.audit_log.controller;

import com.aivle.big_project.audit_log.service.AuditLogService;
import com.aivle.big_project.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> listByUser(
            @PathVariable UUID userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {

        UUID adminId = SecurityUtils.currentUserId();

        return ResponseEntity.ok(
                auditLogService.search(
                        adminId,
                        userId,
                        action,
                        targetType,
                        from,
                        to
                )
        );
    }
}