package com.aivle.big_project.report.controller;

import com.aivle.big_project.report.dto.*;
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
    * (중간) 수사 보고
    * */
    // 수사 보고 초안 생성 (선택한 로그 ID는 body에)
    @PostMapping("/cases/{caseId}/reports/interim/draft")
    public ResponseEntity<?> generateInterimDraft(
            @PathVariable UUID caseId,
            @RequestBody InterimReportGenerateRequest req
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.generateInterimReport(caseId, req.getSelectedLogIds(), userId));
    }

    // 수사 보고 작성완료
    @PostMapping("/cases/{caseId}/reports/interim")
    public ResponseEntity<?> finalizeInterim(
            @PathVariable UUID caseId,
            @RequestBody ReportFinalizeRequest req
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.finalizeInterim(caseId, req, userId));
    }

    /*
    * 수사 결과 보고
    * */

    // 수사 결과 보고 초안 생성
    @GetMapping("/cases/{caseId}/reports/final")
    public ResponseEntity<?> generateFinalDraft(@PathVariable UUID caseId) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.generateFinalReport(caseId, userId));
    }

    // 수사 결과 보고 작성완료
    @PostMapping("/cases/{caseId}/reports/final")
    public ResponseEntity<?> finalizeFinal(
            @PathVariable UUID caseId,
            @RequestBody ReportFinalizeRequest req
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.finalizeFinal(caseId, req, userId));
    }

    /*
    * 수사 결정서 (송치 / 불송치 / 수사중지)
    * */

    // 수사 결정서 초안 생성
    @GetMapping("/cases/{caseId}/reports/decision")
    public ResponseEntity<ReportGenerateResponse> generateDecisionDraft(
            @PathVariable UUID caseId,
            @RequestParam(name = "decision_type") String decisionType
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.generateDecisionDraft(caseId, decisionType, userId));
    }

    // 수사 결정서 작성완료
    @PostMapping("/cases/{caseId}/reports/decision")
    public ResponseEntity<ReportDetailResponse> finalizeDecision(
            @PathVariable UUID caseId,
            @RequestParam(name = "decision_type") String decisionType,
            @RequestBody ReportFinalizeRequest req
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.finalizeDecision(caseId, decisionType, req, userId));
    }

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
            @RequestParam(value = "type", required = false) String type
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.listReportsByCase(caseId, userId, type));
    }

    // 내 보고서 목록 조회
    @GetMapping("/reports/user")
    public ResponseEntity<?> listByUser(
            @RequestParam(value = "type", required = false) String type
    ) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(reportService.listReportsByUser(userId, type));
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
