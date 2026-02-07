package com.aivle.big_project.analysis.dto;

import com.aivle.big_project.investigationFile.entity.FileType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AnalysisDetailResponse {

    private final UUID analysisId;
    private final FileType fileType;
    private final Object data;

    public AnalysisDetailResponse(UUID analysisId, FileType fileType, Object data) {
        this.analysisId = analysisId;
        this.fileType = fileType;
        this.data = data;
    }
}