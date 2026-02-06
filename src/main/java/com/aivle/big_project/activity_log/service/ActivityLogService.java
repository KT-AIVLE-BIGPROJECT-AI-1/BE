package com.aivle.big_project.activity_log.service;

import com.aivle.big_project.activity_log.dto.ActivityLogListResponse;
import com.aivle.big_project.activity_log.entity.ActivityLog;
import com.aivle.big_project.activity_log.repository.ActivityLogRepository;
import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final CaseRepository caseRepository;

    public ActivityLogListResponse listActivityLogs(UUID caseId, UUID userId) {
        CaseEntity aCase = caseRepository.findById(caseId).orElseThrow();
        assertAssignee(aCase, userId);

        List<ActivityLog> logs = activityLogRepository.findByACase_CaseIdOrderByCreatedAtDesc(caseId);
        return ActivityLogListResponse.from(logs);
    }

    private void assertAssignee(CaseEntity aCase, UUID userId) {
        if (aCase.getUser() == null || aCase.getUser().getUserId() == null) {
            throw new RuntimeException("사건 담당자 정보가 없습니다.");
        }
        if (!aCase.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("사건 담당자만 접근할 수 있습니다.");
        }
    }
}
