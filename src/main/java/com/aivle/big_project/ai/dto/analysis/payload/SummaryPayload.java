package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter @Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SummaryPayload {
    private String summary;
    private Double confidence;
    private List<String> keywords;
}
