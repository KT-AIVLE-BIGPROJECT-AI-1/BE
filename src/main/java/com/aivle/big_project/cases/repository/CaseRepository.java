package com.aivle.big_project.cases.repository;

import com.aivle.big_project.cases.entity.Case;
import com.aivle.big_project.cases.entity.CaseStatus;
import com.aivle.big_project.cases.entity.CaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface CaseRepository extends JpaRepository<Case, UUID>, JpaSpecificationExecutor<Case> {
    // 내 사건
    List<Case> findAllByUser_UserIdOrderByUpdatedAtDesc(UUID userId);

    // 우리서 사건
    List<Case> findAllByUser_JurisdictionStationOrderByUpdatedAtDesc(String jurisdictionStation);

    // 대시보드 카운트(내 사건)
    long countByUser_UserIdAndStatus(UUID userId, CaseStatus status);

    // 대시보드 카운트(우리서)
    long countByUser_JurisdictionStationAndStatus(String jurisdictionStation, CaseStatus status);

    // 사건 유형별 카운트
    long countByUser_UserIdAndType(UUID userId, CaseType type);
}
