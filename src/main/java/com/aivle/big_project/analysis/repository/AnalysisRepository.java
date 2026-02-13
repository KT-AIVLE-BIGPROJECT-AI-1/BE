package com.aivle.big_project.analysis.repository;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.entity.AnalysisStatus;
import com.aivle.big_project.investigationFile.entity.FileType;
import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnalysisRepository extends JpaRepository<AnalysisEntity, UUID> {

    @Query("""
        select a
        from AnalysisEntity a
        join fetch a.file f
        where a.analysisId = :analysisId
    """)
    Optional<AnalysisEntity> findByIdWithFile(@Param("analysisId") UUID analysisId);

    @Query("""
        select a
        from AnalysisEntity a
        join a.file f
        where f.caseId = :caseId
          and f.fileType = :fileType
          and a.status = :status
    """)
    Optional<AnalysisEntity> findCompletedAudioAnalysisByCaseId(
            @Param("caseId") UUID caseId,
            @Param("fileType") FileType fileType,
            @Param("status") AnalysisStatus status
    );

    @Query("""
        select count(a) > 0
        from AnalysisEntity a
        join a.file f
        where f.caseId = :caseId
          and f.fileType = :fileType
          and a.status = :status
    """)
    boolean existsCompletedAnalysisByCaseIdAndType(
            @Param("caseId") UUID caseId,
            @Param("fileType") FileType fileType,
            @Param("status") AnalysisStatus status
    );

    Optional<AnalysisEntity> findByFile(InvestigationFileEntity f);

    List<AnalysisEntity> findAllByStatusNotAndFile_FileType(AnalysisStatus analysisStatus, FileType fileType);

    List<AnalysisEntity> findAllByFile_CaseIdOrderByCreatedAtAsc(UUID caseId);
}