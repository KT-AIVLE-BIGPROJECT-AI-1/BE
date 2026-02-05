package com.aivle.big_project.criteria.dto;

import com.aivle.big_project.criteria.entity.CriteriaEvaluationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class UpdateCriteriaEvaluationRequest {

    @NotNull
    private CriteriaEvaluationStatus status;

    @NotBlank
    private String reason;

    private List<String> evidenceSupplementPoints;
}
