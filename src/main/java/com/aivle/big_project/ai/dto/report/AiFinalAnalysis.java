package com.aivle.big_project.ai.dto.report;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AiFinalAnalysis {
    private UUID finalId;
    private UUID caseId;
    private String recommenedDecision;
    private String decisionSummary;
    private String legalIssueSummary;
    private String evidenceSufficiencySummary;
    private String remainingRiskSummary;
    private Instant createdAt;
}
