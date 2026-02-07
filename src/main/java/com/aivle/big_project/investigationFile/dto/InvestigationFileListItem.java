package com.aivle.big_project.investigationFile.dto;

import com.aivle.big_project.analysis.entity.AnalysisStatus;
import com.aivle.big_project.investigationFile.entity.FileType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class InvestigationFileListItem {

    private UUID fileId;
    private String fileType;
    private String originName;
    private LocalDateTime createdAt;

    private Analysis analysis; // 👈 프론트가 원하는 구조

    public InvestigationFileListItem(
            UUID fileId,
            FileType fileType,
            String originName,
            LocalDateTime createdAt,
            UUID analysisId,
            AnalysisStatus status
    ) {
        this.fileId = fileId;
        this.fileType = fileType.name();
        this.originName = originName;
        this.createdAt = createdAt;

        if (analysisId != null) {
            this.analysis = new Analysis(analysisId, status);
        }
    }

    @Getter
    public static class Analysis {
        private UUID analysisId;
        private AnalysisStatus status;

        public Analysis(UUID analysisId, AnalysisStatus status) {
            this.analysisId = analysisId;
            this.status = status;
        }
    }
}