package com.aivle.big_project.report.service;

import com.aivle.big_project.activity_log.entity.ActivityLog;
import com.aivle.big_project.activity_log.repository.ActivityLogRepository;

import com.aivle.big_project.cases.entity.Case;
import com.aivle.big_project.cases.repository.CaseRepository;
import com.aivle.big_project.report.dto.*;
import com.aivle.big_project.report.entity.Report;
import com.aivle.big_project.report.entity.ReportStatus;
import com.aivle.big_project.report.entity.ReportType;
import com.aivle.big_project.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final CaseRepository caseRepository;
    private final ActivityLogRepository activityLogRepository;

    // 보고서 임시저장 (모든 보고서에 대하여)
    @Transactional
    public ReportDetailResponse createDraft(
            UUID caseId,
            ReportDraftCreateRequest req,
            UUID userId
    ) {
        Case aCase = caseRepository.findById(caseId).orElseThrow();
        assertAssignee(aCase, userId); // 사건 - 사용자 간 권한 검사

        ReportType type = parseType(req.getType());

        // 보고서 type 별 필수값 검증
        assertField(type, req.getSelectedLogIds(), req.getFinalId());

        Report report = Report.builder()
                .aCase(aCase)
                .type(type)
                .status(ReportStatus.DRAFT)
                .title(req.getTitle())
                .content(req.getContent())
                .filePath(null)
                .finalId(req.getFinalId())
                .build();

        Report saved = reportRepository.save(report);

        if (type == ReportType.INTERIM) {
            // 포함된 모든 activity_log에 대해 report_id 연결해 귀속시키기
            // 새로 선택된 것 모두 attach
            List<ActivityLog> incoming = activityLogRepository.findAllById(req.getSelectedLogIds());
            for(ActivityLog log : incoming) {
                log.setReport(report);
            }
            activityLogRepository.saveAll(incoming);
        }

        return toDetail(saved);
    }

    // 보고서 임시저장 업데이트
    @Transactional
    public ReportDetailResponse updateDraft(
            UUID caseId,
            UUID reportId,
            ReportDraftUpdateRequest req,
            UUID userId
    ) {
        Case aCase = caseRepository.findById(caseId).orElseThrow();
        assertAssignee(aCase, userId);

        Report report = reportRepository.findById(reportId).orElseThrow();
        assertSameCase(caseId, report);

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new RuntimeException("이미 작성 완료된 보고서는 수정할 수 없습니다.");
        }

        ReportType type = report.getType();

        report.updateDraft(req.getTitle(), req.getContent());

        if (type == ReportType.INTERIM) syncReportLogs(caseId, report, req.getSelectedLogIds());
        else {
            // 사건에서 가장 최신의 통합분석 가져오기
//            UUID latestFinalId = finalAnalysisRepository.findTopByACase_CaseIdOrderByCreatedAtDesc(caseId)
//                                                        .map(fa -> fa.getFinalId())
//                                                        .orElseThrow(() -> new RuntimeException("최종 통합분석이 존재하지 않아 결과보고/결정서를 수정할 수 없습니다."));
//            report.setFinalId(latestFinalId);
        }
        return toDetail(report);
    }

    // 사건별 보고서 목록
    public ReportListResponse listReportsByCase(UUID caseId, UUID userId, Integer page, Integer size) {
        Case aCase = caseRepository.findById(caseId).orElseThrow();
        assertAssignee(aCase, userId);

        int safePage = Math.max(page, 1);
        int safeSize = size <= 0 ? 20 : size;

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );
        Page<Report> result = reportRepository.findByACase_CaseId(caseId, pageable);

        return ReportListResponse.builder()
                .reports(result.getContent().stream().map(this::toSummary).toList())
                .info(PageInfo.builder()
                        .page(safePage)
                        .size(safeSize)
                        .totalElements(result.getTotalElements())
                        .totalPages(result.getTotalPages())
                        .hasNext(result.hasNext())
                        .build())
                .build();
    }

    // 내 보고서 목록
    public ReportListResponse listReportsByUser(UUID userId, Integer page, Integer size) {
        // 기준 = 내가 담당자인 사건의 보고서

        int safePage = Math.max(page, 1);
        int safeSize = size <= 0 ? 20 : size;

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        Page<Report> result = reportRepository.findByACase_User_UserId(userId, pageable);

        return ReportListResponse.builder()
                .reports(result.getContent().stream().map(this::toSummary).toList())
                .info(PageInfo.builder()
                        .page(safePage)
                        .size(safeSize)
                        .totalElements(result.getTotalElements())
                        .totalPages(result.getTotalPages())
                        .hasNext(result.hasNext())
                        .build())
                .build();
    }

    // 임시저장 불러오기
    public ReportDetailResponse getDraft(UUID caseId, UUID reportId, UUID userId) {
        Case aCase = caseRepository.findById(caseId).orElseThrow();
        assertAssignee(aCase, userId);

        Report report = reportRepository.findById(reportId).orElseThrow();
        assertSameCase(caseId, report);

        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new RuntimeException("임시저장(DRAFT) 문서가 아닙니다.");
        }

        return toDetail(report);
    }

    // 보고서 상세 조회
    public ReportDetailResponse getReportDetail(UUID reportId, UUID userId) {
        Report report = reportRepository.findById(reportId).orElseThrow();
        return toDetail(report);
    }

    private void assertField(ReportType type, List<UUID> selectedLogIds, UUID finalId) {
        if (type == ReportType.INTERIM) { // 수사보고 -> 사건활동로그 id 리스트
            if (selectedLogIds == null || selectedLogIds.isEmpty()) {
                throw new RuntimeException("수사보고 문서는 selected_log_ids가 필요합니다.");
            }
        } else { // 결과보고+결정서 -> 최종분석결과 id
            if (finalId == null) {
                throw new RuntimeException("수사결과보고/수사결정서는 final_id가 필요합니다.");
            }
        }
    }

    private void syncReportLogs(UUID caseId, Report report, List<UUID> selectedLogIds) {
        List<UUID> incomingIds = (selectedLogIds == null) ? List.of() : selectedLogIds.stream().distinct().toList();

        if (incomingIds.isEmpty()) throw new RuntimeException("selected_log_ids는 비어있을 수 없습니다.");

        // 현재 이 (임시)보고서에 귀속된 로그들
        List<ActivityLog> existing = activityLogRepository.findByReport_ReportId(report.getReportId());
        // 새로 선택된 로그들
        List<ActivityLog> incoming = activityLogRepository.findAllById(incomingIds);

        if (incoming.size() != incomingIds.size()) throw new RuntimeException("존재하지 않는 activity_log_id가 포함되어 있습니다.");

        for (ActivityLog log : incoming) {
            // 같은 사건의 로그만 허용
            if (log.getACase() == null || log.getACase().getCaseId() == null) throw new RuntimeException("activity_log에 사건 정보가 없습니다.");
            if (!log.getACase().getCaseId().equals(caseId)) throw new RuntimeException("다른 사건의 activity_log는 선택할 수 없습니다.");
        }

        // 기존 선택 - 새로 선택 => detach
        HashSet<UUID> incomingSet = new HashSet<>(incomingIds);
        for(ActivityLog log : existing) {
            if (!incomingSet.contains(log.getLogId())) log.setReport(null);
        }
        // 새로 선택된 것 모두 attach
        for(ActivityLog log : incoming) {
            log.setReport(report);
        }
        activityLogRepository.saveAll(existing);
        activityLogRepository.saveAll(incoming);
    }


    private void assertAssignee(Case aCase, UUID userId) {
        if (aCase.getUser() == null || aCase.getUser().getUserId() == null) {
            throw new RuntimeException("사건 담당자 정보가 없습니다.");
        }
        if (!aCase.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("사건 담당자만 접근할 수 있습니다.");
        }
    }

    private void assertSameCase(UUID caseId, Report report) {
        if (!report.getACase().getCaseId().equals(caseId)) {
            throw new RuntimeException("case_id와 report_id가 일치하지 않습니다.");
        }
    }

    private ReportType parseType(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("type은 필수입니다.");
        }
        try {
            return ReportType.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("type 값이 올바르지 않습니다: " + raw);
        }
    }

    private ReportDetailResponse toDetail(Report report) {
        List<UUID> selectedLogIds = List.of();

        if (report.getType() == ReportType.INTERIM) {
            selectedLogIds = activityLogRepository.findByReport_ReportId(report.getReportId())
                    .stream()
                    .map(log -> log.getLogId())
                    .toList();
        }
        return ReportDetailResponse.builder()
                .reportId(report.getReportId())
                .caseId(report.getACase().getCaseId())
                .finalId(report.getFinalId())
                .type(report.getType().name())
                .status(report.getStatus().name())
                .title(report.getTitle())
                .content(report.getContent())
                .filePath(report.getFilePath())
                .selectedLogIds(selectedLogIds)
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();


    }
    private ReportSummaryResponse toSummary(Report report) {
        return ReportSummaryResponse.builder()
                .reportId(report.getReportId())
                .caseId(report.getACase().getCaseId())
                .type(report.getType().name())
                .status(report.getStatus().name())
                .title(report.getTitle())
                .filePath(report.getFilePath())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}
