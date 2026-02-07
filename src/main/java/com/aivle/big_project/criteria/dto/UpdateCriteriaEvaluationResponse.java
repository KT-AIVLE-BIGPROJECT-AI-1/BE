package com.aivle.big_project.criteria.dto;

import com.aivle.big_project.criteria.entity.CriteriaEvaluationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateCriteriaEvaluationResponse {

    private UUID criteriaId;
    private CriteriaEvaluationStatus status;
    private LocalDateTime updatedAt;
}
