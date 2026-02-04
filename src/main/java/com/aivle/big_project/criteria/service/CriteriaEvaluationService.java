package com.aivle.big_project.criteria.service;

import com.aivle.big_project.criteria.dto.CriteriaEvaluationStatusResponse;
import com.aivle.big_project.criteria.dto.UpdateCriteriaEvaluationRequest;
import com.aivle.big_project.criteria.dto.UpdateCriteriaEvaluationResponse;
import com.aivle.big_project.criteria.entity.CriteriaEntity;
import com.aivle.big_project.criteria.entity.CriteriaEvaluationEntity;
import com.aivle.big_project.criteria.repository.CriteriaEvaluationRepository;
import com.aivle.big_project.criteria.repository.CriteriaRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CriteriaEvaluationService {

    private final CriteriaRepository criteriaRepository;
    private final CriteriaEvaluationRepository criteriaEvaluationRepository;

    public CriteriaEvaluationStatusResponse getCriteriaEvaluationStatus(
            UUID caseId,
            UUID criteriaId
    ) {

        // 사건에 속한 판단 기준인지 검증
        CriteriaEntity criteria = criteriaRepository
                .findByCaseIdAndCriteriaId(caseId, criteriaId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.CRITERIA_NOT_FOUND)
                );

        CriteriaEvaluationEntity evaluation = criteriaEvaluationRepository
                .findByCriteria_CriteriaId(criteriaId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "CriteriaEvaluationEntity must exist for criteriaId=" + criteriaId
                        )
                );

        return new CriteriaEvaluationStatusResponse(
                criteria.getCriteriaId(),
                evaluation.getStatus(),
                evaluation.getUpdatedAt()
        );
    }

    @Transactional
    public UpdateCriteriaEvaluationResponse updateCriteriaEvaluation(
            UUID caseId,
            UUID criteriaId,
            UpdateCriteriaEvaluationRequest request
    ) {

        // 1. 사건에 속한 판단 기준인지 검증
        CriteriaEntity criteria = criteriaRepository
                .findByCaseIdAndCriteriaId(caseId, criteriaId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.CRITERIA_NOT_FOUND)
                );

        // 2. 평가 엔티티 조회 (항상 존재한다고 가정)
        CriteriaEvaluationEntity evaluation = criteriaEvaluationRepository
                .findByCriteria_CriteriaId(criteriaId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.CRITERIA_EVALUATION_NOT_FOUND)
                );

        // 3. 필수값 검증
        if (request.getStatus() == null || request.getReason().isBlank()) {
            throw new CustomException(ErrorCode.INVALID_CRITERIA_EVALUATION_REQUEST);
        }

        // 4. 평가 수정 (🔥 핵심)
        evaluation.updateEvaluation(
                request.getStatus(),
                request.getReason(),
                request.getEvidenceSupplementPoints() == null
                        ? null
                        : String.join("\n", request.getEvidenceSupplementPoints())
        );

        // 5. 응답
        return new UpdateCriteriaEvaluationResponse(
                criteria.getCriteriaId(),
                evaluation.getStatus(),
                evaluation.getUpdatedAt()
        );
    }
}
