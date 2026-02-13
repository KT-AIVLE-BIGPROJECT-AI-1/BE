package com.aivle.big_project.ai.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor @ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AiFinalAnalysis {
    private UUID finalId;
    private UUID caseId;
    private String recommendedDecision;
    private String decisionSummary;
    private String legalIssuesSummary;
    private String evidenceSufficiencySummary;
    private String remainingRiskSummary;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String createdAt;
}
