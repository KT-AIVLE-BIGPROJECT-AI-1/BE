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

import java.time.LocalDateTime;
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
                evaluation.getCreatedAt()
        );
    }

    @Transactional
    public UpdateCriteriaEvaluationResponse updateCriteriaEvaluation(
            UUID caseId,
            UUID criteriaId,
            UpdateCriteriaEvaluationRequest request
    ) {

        CriteriaEntity criteria = criteriaRepository
                .findByCaseIdAndCriteriaId(caseId, criteriaId)
                .orElseThrow(() -> new CustomException(ErrorCode.CRITERIA_NOT_FOUND));

        CriteriaEvaluationEntity latest = criteriaEvaluationRepository
                .findTopByCriteria_CriteriaIdOrderByCreatedAtDesc(criteriaId)
                .orElse(null);

        String evidenceGap =
                switch (request.getStatus()) {
                    case NOT_SATISFIED, INSUFFICIENT -> {
                        if (request.getEvidenceSupplementPoints() != null) {
                            yield String.join("\n", request.getEvidenceSupplementPoints());
                        }
                        yield latest != null
                                ? latest.getEvidenceGap()
                                : null;
                    }
                    default -> null;
                };

        CriteriaEvaluationEntity newEvaluation =
                criteriaEvaluationRepository.save(
                        CriteriaEvaluationEntity.builder()
                                .criteria(criteria)
                                .status(request.getStatus())
                                .reason(request.getReason())
                                .evidenceGap(evidenceGap)
                                .createdAt(LocalDateTime.now())
                                .build()
                );

        return new UpdateCriteriaEvaluationResponse(
                newEvaluation.getEvaluationId(),
                criteria.getCriteriaId(),
                newEvaluation.getStatus(),
                newEvaluation.getReason(),
                request.getEvidenceSupplementPoints(),
                newEvaluation.getCreatedAt()
        );
    }
}
