package com.aivle.big_project.analysis.repository;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.entity.AnalysisStatus;
import com.aivle.big_project.investigationFile.entity.FileType;
import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select a
        from AnalysisEntity a
        join fetch a.file f
        where a.analysisId = :analysisId
    """) // 동일 analysisId에 대한 콜백을 직렬화하기 위한 잠금 조회
    // AUDIO: Ttanscript - Key_Statement - Contradiction - Question 순 temp_map 의존성
    // 콜백이 동시에 들어오면 404 매핑누락 발생 가능
    Optional<AnalysisEntity> findByIdForUpdate(
            @Param("analysisId") UUID analysisId
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

    List<AnalysisEntity> findAllByFile_CaseId(UUID caseId);
}