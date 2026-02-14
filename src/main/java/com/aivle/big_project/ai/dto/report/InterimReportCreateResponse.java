package com.aivle.big_project.ai.dto.report;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InterimReportCreateResponse {
    private String title;
    private InterimReportContent content;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class InterimReportContent {
    private String caseOverview;
    private String investigationProgress;
    private String futurePlan;
    private CurrentStatus currentStatus;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class CurrentStatus {
    private String collectedEvidence;
    private String legalReview;
}
