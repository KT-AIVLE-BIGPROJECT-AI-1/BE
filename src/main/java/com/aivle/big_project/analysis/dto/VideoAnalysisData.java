package com.aivle.big_project.analysis.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class VideoAnalysisData {

    private final String presignedUrl;
    private final String resultData;
    private final List<VideoEventResponse> events;

    public VideoAnalysisData(String presignedUrl, String resultData, List<VideoEventResponse> events) {
        this.presignedUrl = presignedUrl;
        this.resultData = resultData;
        this.events = events;
    }
}