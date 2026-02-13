package com.aivle.big_project.activity_log.repository;

import com.aivle.big_project.activity_log.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    // 사건별 로그 목록
    List<ActivityLog> findByCaseEntity_CaseId(UUID caseId);

    // 보고서 내 포함된 로그
    List<ActivityLog> findByReport_ReportId(UUID reportId);

    // 최신순 정렬
    List<ActivityLog> findByCaseEntity_CaseIdOrderByCreatedAtDesc(UUID caseId);
}
