package com.aivle.big_project.ai.dto.finalAnalysis;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@Builder @ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AiFinalAnalysisResponse {
    private String recommendedDecision;
    private String decisionSummary;
    private String legalIssuesSummary;
    private String evidenceSufficiencySummary;
    private String remainingRiskSummary;
}
