package com.aivle.big_project.criteria.dto;

import com.aivle.big_project.criteria.entity.CriteriaEvaluationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriteriaEvaluationStatusResponse {

    private UUID criteriaId;
    private CriteriaEvaluationStatus status;
    private LocalDateTime createdAt;
}
