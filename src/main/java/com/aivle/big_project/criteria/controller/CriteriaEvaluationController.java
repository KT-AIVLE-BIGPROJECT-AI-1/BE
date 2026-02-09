package com.aivle.big_project.criteria.controller;

import com.aivle.big_project.criteria.dto.CriteriaEvaluationStatusResponse;
import com.aivle.big_project.criteria.dto.UpdateCriteriaEvaluationRequest;
import com.aivle.big_project.criteria.dto.UpdateCriteriaEvaluationResponse;
import com.aivle.big_project.criteria.service.CriteriaEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cases/{caseId}/criteria")
public class CriteriaEvaluationController {

    private final CriteriaEvaluationService criteriaEvaluationService;

    @GetMapping("/{criteriaId}/status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<CriteriaEvaluationStatusResponse> getCriteriaEvaluationStatus(
            @PathVariable UUID caseId,
            @PathVariable UUID criteriaId
    ) {
        return ResponseEntity.ok(
                criteriaEvaluationService.getCriteriaEvaluationStatus(caseId, criteriaId)
        );
    }

    @PostMapping("/{criteriaId}")
    public ResponseEntity<UpdateCriteriaEvaluationResponse> updateCriteriaEvaluation(
            @PathVariable UUID caseId,
            @PathVariable UUID criteriaId,
            @RequestBody @Valid UpdateCriteriaEvaluationRequest request
    ) {
        return ResponseEntity.ok(
                criteriaEvaluationService.updateCriteriaEvaluation(caseId, criteriaId, request)
        );
    }
}

