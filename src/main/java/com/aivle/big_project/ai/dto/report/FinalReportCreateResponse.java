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
public class FinalReportCreateResponse {
    private String title;
    private FinalReportContent content;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class FinalReportContent {
    private String caseName;
    private String suspect;
    private String charge;
    private CaseOverview caseOverview;
    private InvestigationResult investigationResult;
    private LegalReview legalReview;
    private TransferOpinion transferOpinion;
    private String specialNotes;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class InvestigationResult {
    private String collectedEvidence;
    private String analysisDetails;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class TransferOpinion {
    private String opinion;
    private String judgementBasis;
    private String remainingRisk;
}