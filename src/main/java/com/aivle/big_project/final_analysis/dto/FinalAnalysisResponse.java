package com.aivle.big_project.final_analysis.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FinalAnalysisResponse {

        private UUID finalId;
        private UUID caseId;

        private String recommendedDecision; // TRANSFER | NON_TRANSFER | SUSPENSION
        private String decisionSummary;
        private String legalIssuesSummary;
        private String evidenceSufficiencySummary;
        private String remainingRiskSummary;

        private Instant createdAt;
}
