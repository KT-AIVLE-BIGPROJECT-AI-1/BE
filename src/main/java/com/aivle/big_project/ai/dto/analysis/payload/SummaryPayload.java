package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class SummaryPayload {
    private String summary;
    private Double confidence;
    private List<String> keywords;
}
