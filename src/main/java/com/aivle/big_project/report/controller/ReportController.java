package com.aivle.big_project.report.controller;

import com.aivle.big_project.report.dto.ReportDetailResponse;
import com.aivle.big_project.report.dto.ReportDraftCreateRequest;
import com.aivle.big_project.report.dto.ReportDraftUpdateRequest;
import com.aivle.big_project.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.aivle.big_project.security.SecurityUtils.currentUserId;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /*
    * 보고서 임시 저장 (생성 / 불러오기 / 업데이트)
    * */
    // 보고서 임시저장 생성
    @PostMapping("/cases/{caseId}/reports")
    public ResponseEntity<?> createDraft(
            @PathVariable UUID caseId,
            @RequestBody ReportDraftCreateRequest req
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.createDraft(caseId, req, userId));
    }

    // 보고서 임시저장 업데이트
    @PutMapping("/cases/{caseId}/reports/{reportId}")
    public ResponseEntity<?> updateDraft(
            @PathVariable UUID caseId,
            @PathVariable UUID reportId,
            @RequestBody ReportDraftUpdateRequest req
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.updateDraft(caseId, reportId, req, userId));
    }

    // 보고서 임시저장 불러오기
    @GetMapping("/cases/{caseId}/reports/{reportId}")
    public ResponseEntity<?> getDraft(
            @PathVariable UUID caseId,
            @PathVariable UUID reportId
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.getDraft(caseId, reportId, userId));
    }

    /*
    * 보고서 목록 조회
    * */

    // 사건별 보고서 목록 조회
    @GetMapping("/cases/{caseId}/reports")
    public ResponseEntity<?> listByCase(
            @PathVariable UUID caseId,
            @RequestParam(defaultValue = "1") Integer page,   // 1-base
            @RequestParam(defaultValue = "20") Integer size
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.listReportsByCase(caseId, userId, page, size));
    }

    // 내 보고서 목록 조회
    @GetMapping("/reports/user")
    public ResponseEntity<?> listByUser(
            @RequestParam(defaultValue = "1") Integer page,   // 1-base
            @RequestParam(defaultValue = "20") Integer size
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.listReportsByUser(userId, page, size));
    }

    // 보고서 상세 조회
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ReportDetailResponse> getDetail(
            @PathVariable UUID reportId
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.getReportDetail(reportId, userId));
    }

//    // 보고서 다운로드
//    @GetMapping("/reports/{reportId}/download")
//    public ResponseEntity<?> download(@PathVariable UUID reportId) {
//        return ResponseEntity.ok(reportService.getDownloadUrl(reportId));
//    }




}
