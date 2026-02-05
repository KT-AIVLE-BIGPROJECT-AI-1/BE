package com.aivle.big_project.analysis.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AnalysisDetailResponse {

    private final UUID analysisId;
    private final String analysisType;
    private final Object data;

    public AnalysisDetailResponse(UUID analysisId, String analysisType, Object data) {
        this.analysisId = analysisId;
        this.analysisType = analysisType;
        this.data = data;
    }
}