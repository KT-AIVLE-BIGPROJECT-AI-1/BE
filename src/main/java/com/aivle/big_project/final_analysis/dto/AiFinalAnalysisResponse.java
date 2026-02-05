package com.aivle.big_project.final_analysis.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AiFinalAnalysisResponse {
    private String recommendedDecision; // TRANSFER | NON_TRANSFER | SUSPENSION
    private String decisionSummary;
    private String legalIssuesSummary;
    private String evidenceSufficiencySummary;
    private String remainingRiskSummary;
}
