package com.aivle.big_project.report.repository;

import com.aivle.big_project.report.entity.Report;
import com.aivle.big_project.report.entity.ReportStatus;
import com.aivle.big_project.report.entity.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByACase_CaseIdOrderByUpdatedAtDesc(UUID caseId);

    List<Report> findByACase_CaseIdAndTypeInOrderByUpdatedAtDesc(UUID caseId, List<ReportType> types);

    List<Report> findByACase_User_UserIdOrderByUpdatedAtDesc(UUID userId);

    List<Report> findByACase_User_UserIdAndTypeInOrderByUpdatedAtDesc(UUID userId, List<ReportType> types);

    Optional<Report> findByACase_CaseIdAndTypeAndStatus(
            UUID caseId,
            ReportType type,
            ReportStatus status
    );

    @Query("""
    SELECT r
    FROM Report r
    JOIN r.aCase c
    JOIN c.user u
    WHERE u.userId = :userId
    ORDER BY r.updatedAt DESC
""")
    List<Report> findReportsByUserId(@Param("userId") UUID userId);

    @Query("""
    SELECT r
    FROM Report r
    JOIN r.aCase c
    JOIN c.user u
    WHERE u.userId = :userId
      AND r.type IN :types
    ORDER BY r.updatedAt DESC
""")
    List<Report> findReportsByUserIdAndTypeIn(
            @Param("userId") UUID userId,
            @Param("types") List<ReportType> types
    );

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

    @Query("""
    select r
    from Report r
    where r.aCase.caseId = :caseId
      and r.type = :type
      and r.status = :status
""")
    Optional<Report> findDraft(
            @Param("caseId") UUID caseId,
            @Param("type") ReportType type,
            @Param("status") ReportStatus status
    );
}
