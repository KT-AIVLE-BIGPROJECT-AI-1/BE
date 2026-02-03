package com.aivle.big_project.cases.controller;

import com.aivle.big_project.cases.dto.CreateCaseRequest;
import com.aivle.big_project.cases.dto.UpdateCaseAssigneeRequest;
import com.aivle.big_project.cases.dto.UpdateCaseDescriptionRequest;
import com.aivle.big_project.cases.dto.UpdateCaseStatusRequest;
import com.aivle.big_project.cases.service.CaseService;
import com.aivle.big_project.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    // 홈화면 대시보드
    @GetMapping("/home")
    public ResponseEntity<?> home(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, name = "start_at")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startAt,
            @RequestParam(required = false, name = "end_at")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endAt
    ) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(
                caseService.getHomeDashboard(page, size, keyword, type, startAt, endAt, userId)
        );
    }

    // 사건 생성
    @PostMapping("/cases")
    public ResponseEntity<?> create(@Valid @RequestBody CreateCaseRequest req) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(caseService.createCase(req, userId));
    }

    // 우리서 내 사건 목록
    @GetMapping("/cases/jurisdiction")
    public ResponseEntity<?> jurisdictionCases(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, name = "start_at")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startAt,
            @RequestParam(required = false, name = "end_at")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endAt
    ) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(caseService.listJurisdictionCases(
                page, size, keyword, type,
                startAt, endAt, userId
        ));
    }

    // 내 사건 목록
    @GetMapping("/cases/user")
    public ResponseEntity<?> myCases(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, name = "start_at")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startAt,
            @RequestParam(required = false, name = "end_at")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime endAt
    ) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(caseService.listMyCases(
                page, size, keyword, type,
                startAt, endAt, userId
        ));
    }

    // 우리서/내 사건 상세 + 사건 개요 조회(상세) 공용
    @GetMapping("/cases/{caseId}")
    public ResponseEntity<?> caseDetail(@PathVariable UUID caseId) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(caseService.getCaseDetail(caseId, userId));
    }


    // 사건 내용 수정
    @PatchMapping("/cases/{caseId}")
    public ResponseEntity<?> updateDescription(
            @PathVariable UUID caseId,
            @Valid @RequestBody UpdateCaseDescriptionRequest req
    ) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(caseService.updateCaseDescription(caseId, req, userId));
    }

    // 사건 상태 수정
    @PatchMapping("/cases/{caseId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID caseId,
            @Valid @RequestBody UpdateCaseStatusRequest req
    ) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(caseService.updateCaseStatus(caseId, req, userId));
    }

    // 사건 삭제(OPEN일 때만)
    @DeleteMapping("/cases/{caseId}")
    public ResponseEntity<?> delete(@PathVariable UUID caseId) {
        UUID userId = SecurityUtils.currentUserId();
        caseService.deleteCase(caseId, userId);
        return ResponseEntity.noContent().build();
    }

    // 사건 담당자 변경(ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/cases/{caseId}/assignee")
    public ResponseEntity<?> changeAssignee(
            @PathVariable UUID caseId,
            @Valid @RequestBody UpdateCaseAssigneeRequest req
    ) {
        UUID userId = SecurityUtils.currentUserId();
        return ResponseEntity.ok(caseService.changeAssignee(caseId, req, userId));
    }
}
