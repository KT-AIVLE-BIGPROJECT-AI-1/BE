package com.aivle.big_project.analysis.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AudioAnalysisData {

    private final String resultData;
    private final List<KeyStatementResponse> keyStatements;

    public AudioAnalysisData(String resultData, List<KeyStatementResponse> keyStatements) {
        this.resultData = resultData;
        this.keyStatements = keyStatements;
    }
}