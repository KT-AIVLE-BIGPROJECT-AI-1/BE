package com.aivle.big_project.cases.service;

import com.aivle.big_project.activity_log.entity.ActivityLogField;
import com.aivle.big_project.activity_log.entity.ActivityType;
import com.aivle.big_project.activity_log.service.ActivityLogService;
import com.aivle.big_project.cases.client.AiSummaryClient;
import com.aivle.big_project.cases.dto.*;
import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.entity.CaseStatus;
import com.aivle.big_project.cases.entity.CaseType;
import com.aivle.big_project.cases.repository.CaseRepository;
import com.aivle.big_project.user.entity.User;
import com.aivle.big_project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseService {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final AiSummaryClient aiSummaryClient;
    private final CaseTabFlagService caseTabFlagService;
    private final ActivityLogService activityLogService;

    // 홈화면 대시보드
    @Transactional
    public HomeDashboardResponse getHomeDashboard(
            Integer page, Integer size, String keyword, String type,
            LocalDateTime startOccurredAt, LocalDateTime endOccurredAt,
            UUID userId)
    {
        User user = userRepository.findById(userId).orElseThrow();

        long open = caseRepository.countByUser_UserIdAndStatus(user.getUserId(), CaseStatus.OPEN);
        long ongoing = caseRepository.countByUser_UserIdAndStatus(user.getUserId(), CaseStatus.ONGOING);
        long done = caseRepository.countByUser_UserIdAndStatus(user.getUserId(), CaseStatus.DONE);

        Map<String, Long> typeCounts = new LinkedHashMap<>();
        for(CaseType t : CaseType.values()) {
            typeCounts.put(t.name(), caseRepository.countByUser_UserIdAndType(user.getUserId(), t));
        }

        CaseType paredType = parseTypeOrNull(type);
        Specification<CaseEntity> spec = Specification.where(CaseSpecifications.assigneeIs(user.getUserId()))
                .and(CaseSpecifications.titleKeyword(keyword))
                .and(CaseSpecifications.typeEquals(paredType))
                .and(CaseSpecifications.occurredAtBetween(startOccurredAt, endOccurredAt));

        int safePage = Math.max(page, 1);
        int safeSize = size <= 0 ? 20 : size;

        Pageable pageable = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        Page<CaseEntity> result = caseRepository.findAll(spec, pageable);

        return HomeDashboardResponse.builder()
                .dashboard(HomeDashboardResponse.Dashboard.builder()
                        .open(open).ongoing(ongoing).done(done)
                        .typeCounts(typeCounts)
                        .build())
                .cases(result.getContent().stream().map(this::toSummary).toList())
                .info(PageInfo.builder()
                        .page(safePage)
                        .size(safeSize)
                        .totalElements(result.getTotalElements())
                        .totalPages(result.getTotalPages())
                        .hasNext(result.hasNext())
                        .build())
                .build();
    }

    // 사건 생성
    @Transactional
    public CaseDetailResponse createCase(CreateCaseRequest req, UUID userId) {

        User user = userRepository.findById(userId).orElseThrow();

        CaseType paredType = parseTypeOrNull(req.getType());
        CaseEntity newCase = CaseEntity.builder()
                .user(user) // 담당자 = 생성자
                .title(req.getTitle())
                .description(req.getDescription())
                .type(paredType)
                .status(CaseStatus.OPEN)
                .occurredAt(req.getOccurredAt())
                .location(req.getLocation())
                .summary(null)
                .build();

        CaseEntity saved = caseRepository.saveAndFlush(newCase);

        activityLogService.log(
                saved.getCaseId(),
                ActivityLogField.CASE,
                ActivityType.CREATE,
                p -> {
                    p.put("title", saved.getTitle());
                    p.put("type", saved.getType().name());
                    p.put("status", saved.getStatus().name());
                    p.put("location", saved.getLocation());
                }
        );

        // AI 호출 내용 - 구조화 요약 실패해도 사건 생성됨
        try {
            AiSummaryClient.SummaryResult result = aiSummaryClient.summarize(
                    AiSummaryClient.SummaryCommand.builder()
                            .title(saved.getTitle())
                            .description(saved.getDescription())
                            .type(saved.getType().name())
                            .location(saved.getLocation())
                            .build()
            );
            if (result != null && result.summaryJson() != null && !result.summaryJson().isBlank()) {
                saved.updateSummary(result.summaryJson());
            }

        } catch (Exception e) {
            log.warn("AI summary generation failed. caseId={}, reason={}",
                    saved.getCaseId(), e.getMessage(), e);
        }
        return toDetail(saved, true);
    }

    // 내 사건 목록
    @Transactional
    public CaseListResponse listMyCases(
            Integer page, Integer size,
            String keyword, String type,
            LocalDateTime startOccurredAt, LocalDateTime endOccurredAt,
            UUID userId
    ) {
        User user = userRepository.findById(userId).orElseThrow();

        CaseType parsedType = parseTypeOrNull(type);

        Specification<CaseEntity> spec = Specification.where(CaseSpecifications.assigneeIs(user.getUserId()))
                .and(CaseSpecifications.titleKeyword(keyword))
                .and(CaseSpecifications.typeEquals(parsedType))
                .and(CaseSpecifications.occurredAtBetween(startOccurredAt, endOccurredAt));

        int safePage = (page == null) ? 1 : Math.max(page, 1);
        int safeSize = (size == null || size <= 0) ? 20 : size;

        Pageable pageable = PageRequest.of(
                safePage - 1, // 1-base -> 0-base
                safeSize,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        Page<CaseEntity> result = caseRepository.findAll(spec, pageable);
        return CaseListResponse.builder()
                .cases(result.getContent().stream().map(this::toSummary).toList())
                .info(PageInfo.builder()
                        .page(safePage)
                        .size(safeSize)
                        .totalElements(result.getTotalElements())
                        .totalPages(result.getTotalPages())
                        .hasNext(result.hasNext())
                        .build())
                .build();
    }

    // 우리서 사건 목록
    @Transactional
    public CaseListResponse listJurisdictionCases(
            Integer page, Integer size,
            String keyword, String type,
            LocalDateTime startOccurredAt, LocalDateTime endOccurredAt,
            UUID userId
    ) {
        User user = userRepository.findById(userId).orElseThrow();

        CaseType parsedType = parseTypeOrNull(type);

        Specification<CaseEntity> spec = Specification.where(
                        CaseSpecifications.assigneeJurisdictionEquals(user.getJurisdictionStation())
                )
                .and(CaseSpecifications.titleKeyword(keyword))
                .and(CaseSpecifications.typeEquals(parsedType))
                .and(CaseSpecifications.occurredAtBetween(startOccurredAt, endOccurredAt));

        int safePage = (page == null) ? 1 : Math.max(page, 1);
        int safeSize = (size == null || size <= 0) ? 20 : size;

        Pageable pageable = PageRequest.of(
                safePage - 1, // 1-base -> 0-base
                safeSize,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        Page<CaseEntity> result = caseRepository.findAll(spec, pageable);

        return CaseListResponse.builder()
                .cases(result.getContent().stream().map(this::toSummary).toList())
                .info(PageInfo.builder()
                        .page(safePage)
                        .size(safeSize)
                        .totalElements(result.getTotalElements())
                        .totalPages(result.getTotalPages())
                        .hasNext(result.hasNext())
                        .build())
                .build();
    }

    // 사건 상세 조회 (기본 개요 탭, 우리서/내 사건 구분)
    @Transactional
    public TabAwareResponse<CaseDetailResponse> getCaseDetail(UUID caseId, UUID userId) {

        User user = userRepository.findById(userId).orElseThrow();
        CaseEntity aCase = caseRepository.findById(caseId).orElseThrow();

        boolean isAssignee = aCase.getUser().getUserId().equals(user.getUserId());
        boolean sameJurisdiction = aCase.getUser().getJurisdictionStation().equals(user.getJurisdictionStation());

        // 우리서 사건 상세 조회
        // 본인 포함 같은 경찰서(관할)에 있으면 조회 가능
        // 본인 X, 같은 관할서 X -> 403
        if(!isAssignee && !sameJurisdiction) {
            throw new RuntimeException("해당 사건에 접근 권한이 없습니다.");
        }

        // 조작 가능 여부는 "내 담당 사건인지"로 결정
        CaseDetailResponse detail = toDetail(aCase, isAssignee);
        TabFlags flags = caseTabFlagService.getFlags(caseId);

        return TabAwareResponse.<CaseDetailResponse>builder()
                .data(detail)
                .tabFlags(flags)
                .build();
    }

    // 사건 내용 수정
    @Transactional
    public TabAwareResponse<CaseDetailResponse> updateCaseDescription(UUID caseId, UpdateCaseDescriptionRequest req, UUID userId) {

        CaseEntity aCase = caseRepository.findById(caseId).orElseThrow();
        boolean isAssignee = aCase.getUser().getUserId().equals(userId);

        if(!isAssignee) throw new RuntimeException("담당자만 수정할 수 있습니다.");
        if(aCase.getStatus() != CaseStatus.OPEN) throw new RuntimeException("분석 이전 상태의 사건만 수정할 수 있습니다.");

        String before = aCase.getDescription();
        aCase.updateDescription(req.getDescription());

        activityLogService.log(
                aCase.getCaseId(),
                ActivityLogField.CASE,
                ActivityType.UPDATE,
                p -> {
                    p.put("field", "description");
                    p.put("before", before);
                    p.put("after", req.getDescription());
                }
        );

        CaseDetailResponse detail = toDetail(aCase, true);
        TabFlags flags = caseTabFlagService.getFlags(caseId);

        return TabAwareResponse.<CaseDetailResponse>builder()
                .data(detail)
                .tabFlags(flags)
                .build();
    }

    // 사건 상태 수정
    @Transactional
    public TabAwareResponse<CaseDetailResponse> updateCaseStatus(UUID caseId, UpdateCaseStatusRequest req, UUID userId) {

        CaseEntity aCase = caseRepository.findById(caseId).orElseThrow();
        boolean isAssignee = aCase.getUser().getUserId().equals(userId);

        if(!isAssignee) throw new RuntimeException("담당자만 상태 변경이 가능합니다.");

        CaseStatus before = aCase.getStatus();
        CaseStatus newStatus = parseStatus(req.getStatus());

        aCase.updateStatus(newStatus);

        activityLogService.log(
                aCase.getCaseId(),
                ActivityLogField.CASE,
                ActivityType.UPDATE,
                p -> {
                    p.put("field", "status");
                    p.put("before", before.name());
                    p.put("after", newStatus.name());
                }
        );

        CaseDetailResponse detail = toDetail(aCase, true);
        TabFlags flags = caseTabFlagService.getFlags(caseId);

        return TabAwareResponse.<CaseDetailResponse>builder()
                .data(detail)
                .tabFlags(flags)
                .build();
    }

    // 사건 삭제
    @Transactional
    public void deleteCase(UUID caseId, UUID userId) {

        CaseEntity aCase = caseRepository.findById(caseId).orElseThrow();
        boolean isAssignee = aCase.getUser().getUserId().equals(userId);

        if(!isAssignee) throw new RuntimeException("담당자만 삭제할 수 있습니다.");
        if(aCase.getStatus() != CaseStatus.OPEN) throw new RuntimeException("분석 이전 상태의 사건만 삭제할 수 있습니다.");

        // 🔥 필요한 값만 미리 복사
        String title = aCase.getTitle();
        String status = aCase.getStatus().name();

        // 1️⃣ 로그 먼저 남기기
        activityLogService.log(
                caseId,
                ActivityLogField.CASE,
                ActivityType.DELETE,
                p -> {
                    p.put("title", title);
                    p.put("status", status);
                }
        );

        // 2️⃣ deleteById 사용 (중요)
        caseRepository.deleteById(caseId);
    }

    // [관리자] 사건 담당자 변경
    @Transactional
    public TabAwareResponse<CaseDetailResponse> changeAssignee(UUID caseId, UpdateCaseAssigneeRequest req, UUID userId) {

        CaseEntity aCase = caseRepository.findById(caseId).orElseThrow();
        User newAssignee = userRepository.findById(req.getAssigneeUserId()).orElseThrow();

        UUID before = aCase.getUser().getUserId();
        aCase.updateUser(newAssignee);

        activityLogService.log(
                aCase.getCaseId(),
                ActivityLogField.CASE,
                ActivityType.UPDATE,
                p -> {
                    p.put("field", "assignee");
                    p.put("before", before);
                    p.put("after", newAssignee.getUserId());
                }
        );

        CaseDetailResponse detail = toDetail(aCase, false);
        TabFlags flags = caseTabFlagService.getFlags(caseId);

        return TabAwareResponse.<CaseDetailResponse>builder()
                .data(detail)
                .tabFlags(flags)
                .build();
    }


    private CaseDetailResponse toDetail(CaseEntity c, boolean canEdit) {
        return CaseDetailResponse.builder()
                .caseId(c.getCaseId())
                .assigneeUserId(c.getUser().getUserId())
                .assigneeUserName(c.getUser().getName())
                .assigneeUserDepartment(c.getUser().getDepartment())
                .assigneeUserRank(c.getUser().getRankName())
                .title(c.getTitle())
                .description(c.getDescription())
                .summary(c.getSummary())
                .type(c.getType().name())
                .status(c.getStatus().name())
                .occurredAt(c.getOccurredAt())
                .location(c.getLocation())
                .canEdit(canEdit)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CaseSummaryResponse toSummary(CaseEntity c) {
        return CaseSummaryResponse.builder()
                .caseId(c.getCaseId())
                .title(c.getTitle())
                .type(c.getType().name())
                .status(c.getStatus().name())
                .occurredAt(c.getOccurredAt())
                .location(c.getLocation())
                .assigneeUserId(c.getUser().getUserId())
                .assigneeName(c.getUser().getName())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CaseType parseTypeOrNull(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return CaseType.valueOf(raw.trim());
        } catch (Exception e) {
            throw new RuntimeException("parseTypeOrNull: type 값이 올바르지 않습니다.");
        }
    }

    private CaseStatus parseStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("status 값이 비어있습니다.");
        }
        try {
            return CaseStatus.valueOf(raw.trim());
        } catch (Exception e) {
            throw new RuntimeException("status 값이 올바르지 않습니다.");
        }
    }

}
