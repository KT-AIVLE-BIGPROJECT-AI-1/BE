package com.aivle.big_project.investigationFile.repository;

import com.aivle.big_project.investigationFile.entity.FileType;
import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
}