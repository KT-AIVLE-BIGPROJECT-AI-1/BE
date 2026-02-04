package com.aivle.big_project.criteria.dto;

import com.aivle.big_project.criteria.entity.CriteriaEvaluationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateCriteriaEvaluationResponse {

    private UUID criteriaId;
    private CriteriaEvaluationStatus status;
    private LocalDateTime updatedAt;
}
