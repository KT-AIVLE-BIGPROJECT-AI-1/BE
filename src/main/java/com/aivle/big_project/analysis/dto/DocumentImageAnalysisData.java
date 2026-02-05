package com.aivle.big_project.analysis.dto;

import lombok.Getter;

@Getter
public class DocumentImageAnalysisData {

    private final String resultData;

    public DocumentImageAnalysisData(String resultData) {
        this.resultData = resultData;
    }
}