package com.aivle.big_project.report.repository;

import com.aivle.big_project.report.entity.Report;
import com.aivle.big_project.report.entity.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByACase_CaseIdOrderByUpdatedAtDesc(UUID caseId);

    List<Report> findByACase_CaseIdAndTypeInOrderByUpdatedAtDesc(UUID caseId, List<ReportType> types);

    List<Report> findByACase_User_UserIdOrderByUpdatedAtDesc(UUID userId);

    List<Report> findByACase_User_UserIdAndTypeInOrderByUpdatedAtDesc(UUID userId, List<ReportType> types);

    @Query("""
    select r
    from Report r
    where r.aCase.caseId = :caseId
    order by r.updatedAt desc
""")
    List<Report> findAllByCaseId(@Param("caseId") UUID caseId);

    @Query("""
    select r
    from Report r
    where r.aCase.caseId = :caseId
      and r.type in :types
    order by r.updatedAt desc
""")
    List<Report> findAllByCaseIdAndTypeIn(
            @Param("caseId") UUID caseId,
            @Param("types") List<ReportType> types
    );
}
