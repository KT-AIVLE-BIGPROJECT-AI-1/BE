package com.aivle.big_project.analysis.dto;

import com.aivle.big_project.analysis.entity.AnalysisStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AnalysisStatusResponse {

    private UUID analysisId;
    private AnalysisStatus status;

    public AnalysisStatusResponse(UUID analysisId, AnalysisStatus status) {
        this.analysisId = analysisId;
        this.status = status;
    }

}