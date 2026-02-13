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
public class DecisionReportCreateResponse {
    private String title;
    private DecisionContent content;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class DecisionContent {
    private String caseName;
    private String suspect;
    private String ruling;
    private CaseOverview caseOverview;
    private JudgementReason judgementReason;
    private EvidenceEvaluation evidenceEvaluation;
    private LegalReview legalReview;
    private Conclusion conclusion;
}
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class JudgementReason {
    private String keyJudgement;
    private String detailedReason;
    private String changeReason;
}
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class EvidenceEvaluation {
    private String collectedEvidence;
    private String evidenceValue;
    private String overallEvaluation;
}
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
class Conclusion {
    private String finalOpinion;
    private String followUp;
    private String remarks;
}

