package com.aivle.big_project.investigationFile.repository;

import com.aivle.big_project.investigationFile.dto.InvestigationFileListItem;
import com.aivle.big_project.investigationFile.entity.FileType;
import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface InvestigationFileRepository
        extends JpaRepository<InvestigationFileEntity, UUID> {
    // type 필터 없음
    List<InvestigationFileEntity>
    findByCaseIdOrderByCreatedAtDesc(UUID caseId);

    // type 필터 있음
    List<InvestigationFileEntity>
    findByCaseIdAndFileTypeOrderByCreatedAtDesc(
            UUID caseId,
            FileType fileType
    );

    boolean existsByCaseIdAndOriginName(UUID caseId, String originName);

    @Query("""
    select new com.aivle.big_project.investigationFile.dto.InvestigationFileListItem(
        f.fileId,
        f.fileType,
        f.originName,
        f.createdAt,
        a.analysisId,
        a.status
    )
    from InvestigationFileEntity f
    left join AnalysisEntity a
        on a.file = f
    where f.caseId = :caseId
    order by f.createdAt desc
""")
    List<InvestigationFileListItem> findFileListWithAnalysis(UUID caseId);
}