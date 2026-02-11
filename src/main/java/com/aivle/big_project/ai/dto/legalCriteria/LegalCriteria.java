package com.aivle.big_project.ai.dto.legalCriteria;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LegalCriteria {
    private String criterionText;
    private String precedentCaseNo;
    private LocalDate precedentDecisionDate;
    private CriteriaEvaluation evaluation;
    private LegalIssue legalIssue;
}
