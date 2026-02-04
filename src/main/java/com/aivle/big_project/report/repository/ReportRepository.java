package com.aivle.big_project.report.repository;

import com.aivle.big_project.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    // 사건별 보고서 목록
    Page<Report> findByACase_CaseId(UUID caseId, Pageable pageable);
    // 사용자별 보고서 목록
    Page<Report> findByACase_User_UserId(UUID userId, Pageable pageable);
}
