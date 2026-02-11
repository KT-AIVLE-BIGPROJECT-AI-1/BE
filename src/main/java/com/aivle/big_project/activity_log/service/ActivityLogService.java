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
import org.springframework.transaction.annotation.Propagation;

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
                activityLogRepository.findByACase_CaseIdOrderByCreatedAtDesc(caseId);

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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(
            UUID caseId,
            ActivityLogField field,
            ActivityType type,
            Consumer<Map<String, Object>> payloadBuilder
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payloadBuilder.accept(payload);

        CaseEntity managedCase =
                caseRepository.findById(caseId)
                        .orElseThrow(() -> new RuntimeException("Case not found"));

        ActivityLog log = ActivityLog.builder()
                .aCase(managedCase)
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
                .caseId(log.getACase().getCaseId())
                .field(log.getField().name())
                .activityType(log.getActivityType().name())
                .payload(log.getPayload())
                .createdAt(log.getCreatedAt())
                .reportId(log.getReport() != null ? log.getReport().getReportId() : null)
                .status(log.getStatus() != null ? log.getStatus().name() : null)
                .build();
    }
}
