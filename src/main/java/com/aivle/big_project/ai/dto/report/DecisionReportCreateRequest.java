package com.aivle.big_project.ai.dto.report;

import com.aivle.big_project.ai.dto.caseSummary.CaseDetail;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DecisionReportCreateRequest {
    private CaseDetail caseDetail;
    private String decisionType;
    private AiFinalAnalysis finalAnalysis;
}
