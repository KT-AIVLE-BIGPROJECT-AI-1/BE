package com.aivle.big_project.activity_log.service;

import com.aivle.big_project.activity_log.dto.ActivityLogListResponse;
import com.aivle.big_project.activity_log.dto.ActivityLogResponse;
import com.aivle.big_project.activity_log.entity.ActivityLog;
import com.aivle.big_project.activity_log.entity.ActivityLogField;
import com.aivle.big_project.activity_log.entity.ActivityLogStatus;
import com.aivle.big_project.activity_log.entity.ActivityType;
import com.aivle.big_project.activity_log.repository.ActivityLogRepository;
import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final CaseRepository caseRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ActivityLogListResponse listActivityLogs(UUID caseId, UUID userId) {

        CaseEntity aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("사건을 찾을 수 없습니다."));

        assertAssignee(aCase, userId);

        List<ActivityLog> logs =
                activityLogRepository.findByCaseEntity_CaseIdOrderByCreatedAtDesc(caseId);

        List<ActivityLogResponse> responses = logs.stream()
                .map(this::toResponse)
                .toList();

        return new ActivityLogListResponse(responses);
    }

    private void assertAssignee(CaseEntity aCase, UUID userId) {
        if (aCase.getUser() == null || aCase.getUser().getUserId() == null) {
            throw new RuntimeException("사건 담당자 정보가 없습니다.");
        }
        if (!aCase.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("사건 담당자만 접근할 수 있습니다.");
        }
    }

    /**
     * 로그 저장 (payload는 Object로 받음)
     */
    @Transactional
    public void log(
            UUID caseId,
            ActivityLogField field,
            ActivityType type,
            Consumer<Map<String, Object>> payloadBuilder
    ) {
        System.out.println("ActivityLog caseId = " + caseId);
        Map<String, Object> payload = new LinkedHashMap<>();
        payloadBuilder.accept(payload);

        CaseEntity managedCase =
                caseRepository.findById(caseId)
                        .orElseThrow(() -> new RuntimeException("Case not found"));

        ActivityLog log = ActivityLog.builder()
                .caseEntity(managedCase)
                .field(field)
                .activityType(type)
                .payload(payload)
                .status(ActivityLogStatus.NOT_INCLUDED)
                .build();

        activityLogRepository.save(log);
    }

    /**
     * Entity → Response 변환
     */
    private ActivityLogResponse toResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .logId(log.getLogId())
                .caseId(log.getCaseEntity().getCaseId())
                .field(log.getField().name())
                .activityType(log.getActivityType().name())
                .payload(log.getPayload())
                .message(buildMessage(log))
                .createdAt(log.getCreatedAt())
                .reportId(log.getReport() != null ? log.getReport().getReportId() : null)
                .status(log.getStatus() != null ? log.getStatus().name() : null)
                .build();
    }

    private String buildMessage(ActivityLog log) {

        Map<String, Object> payload = log.getPayload();
        if (payload == null) return "";

        switch (log.getField()) {

            case CONTRADICTION -> {
                String before = (String) payload.get("before");
                String after = (String) payload.get("after");
                return String.format("모순 상태 변경: %s → %s", before, after);
            }

            case CASE_FILE -> {
                String originName = (String) payload.get("originName");
                String fileType = (String) payload.get("fileType");

                return String.format("파일 업로드: [%s] %s", fileType, originName);
            }

            case CASE -> {
                String field = (String) payload.get("field");
                String before = (String) payload.get("before");
                String after = (String) payload.get("after");

                if ("description".equals(field)) {
                    return "사건 내용 수정";
                }

                if ("status".equals(field)) {
                    return String.format("사건 상태 변경: %s → %s", before, after);
                }

                if ("assignee".equals(field)) {
                    return String.format("담당자 변경: %s → %s", before, after);
                }

                return "사건 정보 수정";
            }

            case CRITERIA_EVALUATION -> {
                String before = (String) payload.get("before");
                String after = (String) payload.get("after");
                String status = (String) payload.get("status");
                String reason = (String) payload.get("reason");
                String gap = (String) payload.get("evidenceGap");

                StringBuilder sb = new StringBuilder();

                sb.append("기준 평가 변경\n");

                if (before != null && after != null) {
                    sb.append(String.format("이전 상태: %s\n", before));
                    sb.append(String.format("변경 상태: %s\n", after));
                } else if (status != null) {
                    sb.append(String.format("상태: %s\n", status));
                }

                if (reason != null && !reason.isBlank()) {
                    sb.append("\n사유: ");
                    sb.append(reason.trim()).append("\n");
                }

                if (gap != null && !gap.isBlank()) {
                    sb.append("\n증거보강 포인트:\n");
                    sb.append(gap.trim());
                }

                return sb.toString();
            }

            default -> {
                return "";
            }
        }
    }
}
