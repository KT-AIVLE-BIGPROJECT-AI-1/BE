package com.aivle.big_project.criteria.dto;

import com.aivle.big_project.criteria.entity.CriteriaEvaluationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateCriteriaEvaluationResponse {

    private UUID evaluationId;
    private UUID criteriaId;
    private CriteriaEvaluationStatus status;
    private String reason;
    private List<String> evidenceSupplementPoints;
    private LocalDateTime createdAt;
}