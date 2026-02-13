package com.aivle.big_project.ai.dto.legalCriteria;

import com.aivle.big_project.ai.dto.analysis.Analysis;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CriteriaEvaluationRequest {
    private Analysis analysis;
    private List<LegalCriteria> legalCriteria;
}
