package com.aivle.big_project.criteria.service;

import com.aivle.big_project.ai.AiClient;
import com.aivle.big_project.ai.dto.analysis.AiCallbackRequest;
import com.aivle.big_project.ai.dto.analysis.Analysis;
import com.aivle.big_project.ai.dto.legalCriteria.CriteriaEvaluationRequest;
import com.aivle.big_project.ai.dto.legalCriteria.CriteriaEvaluationResponse;
import com.aivle.big_project.ai.dto.legalCriteria.LegalCriteria;
import com.aivle.big_project.ai.dto.legalCriteria.LegalIssue;
import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.activity_log.entity.ActivityLogField;
import com.aivle.big_project.activity_log.entity.ActivityType;
import com.aivle.big_project.activity_log.service.ActivityLogService;
import com.aivle.big_project.criteria.dto.CriteriaEvaluationStatusResponse;
import com.aivle.big_project.criteria.dto.UpdateCriteriaEvaluationRequest;
import com.aivle.big_project.criteria.dto.UpdateCriteriaEvaluationResponse;
import com.aivle.big_project.criteria.entity.CriteriaEntity;
import com.aivle.big_project.criteria.entity.CriteriaEvaluationEntity;
import com.aivle.big_project.criteria.entity.CriteriaEvaluationStatus;
import com.aivle.big_project.criteria.entity.LegalIssueEntity;
import com.aivle.big_project.criteria.repository.CriteriaEvaluationRepository;
import com.aivle.big_project.criteria.repository.CriteriaRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service @Slf4j
@RequiredArgsConstructor
public class CriteriaEvaluationService {

    private final CriteriaRepository criteriaRepository;
    private final CriteriaEvaluationRepository criteriaEvaluationRepository;
    private final AnalysisRepository analysisRepository;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final ActivityLogService activityLogService;

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

        CriteriaEvaluationEntity evaluation =
                criteriaEvaluationRepository
                        .findByCriteria_CriteriaIdOrderByCreatedAtDesc(criteriaId)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new CustomException(ErrorCode.CRITERIA_EVALUATION_NOT_FOUND));

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

        CriteriaEvaluationEntity latest =
                criteriaEvaluationRepository
                        .findByCriteria_CriteriaIdOrderByCreatedAtDesc(criteriaId)
                        .stream()
                        .findFirst()
                        .orElse(null);

        CriteriaEvaluationStatus beforeStatus =
                latest != null ? latest.getStatus() : null;

        String evidenceGap =
                switch (request.getStatus()) {
                    case NOT_SATISFIED, INSUFFICIENT -> {
                        if (request.getEvidenceSupplementPoints() != null) {
                            yield String.join("\n", request.getEvidenceSupplementPoints());
                        }
                        yield (latest != null &&
                                (latest.getStatus() == CriteriaEvaluationStatus.NOT_SATISFIED
                                        || latest.getStatus() == CriteriaEvaluationStatus.INSUFFICIENT))
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

        activityLogService.log(
                caseId,
                ActivityLogField.CRITERIA_EVALUATION,
                ActivityType.UPDATE,
                p -> {
                    p.put("criteriaId", criteria.getCriteriaId());
                    if (beforeStatus != null) {
                        p.put("before", beforeStatus.name());
                    }
                    p.put("after", newEvaluation.getStatus().name());

                    if (newEvaluation.getReason() != null) {
                        p.put("reason", newEvaluation.getReason());
                    }

                    if (newEvaluation.getEvidenceGap() != null) {
                        p.put("evidenceGap", newEvaluation.getEvidenceGap());
                    }
                }
        );

        return new UpdateCriteriaEvaluationResponse(
                newEvaluation.getEvaluationId(),
                criteria.getCriteriaId(),
                newEvaluation.getStatus(),
                newEvaluation.getReason(),
                splitEvidenceGap(newEvaluation.getEvidenceGap()),
                newEvaluation.getCreatedAt()
        );
    }

    @Transactional
    public void reevaluateAllByAnalysis(UUID caseId, UUID analysisId) {

        AnalysisEntity analysisEntity = analysisRepository
                .findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_NOT_FOUND));

        // analysis가 해당 사건 소속인지 검증
        if (analysisEntity.getFile() == null
                || analysisEntity.getFile().getCaseId() == null
                || !analysisEntity.getFile().getCaseId().equals(caseId)) {
            throw new CustomException(ErrorCode.ANALYSIS_NOT_IN_CASE);
        }

        JsonNode aggregatedResult;
        try {
            String raw = analysisEntity.getResultData();
            if (raw == null || raw.isBlank()) throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
            aggregatedResult = objectMapper.readTree(raw);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
        }

        Analysis aiAnalysis = Analysis.builder()
                .analysisId(analysisId)
                .fileType(analysisEntity.getFile().getFileType().name())
                .status(analysisEntity.getStatus().name())
                .resultData(aggregatedResult)
                .build();

        List<CriteriaEntity> criteriaList =
                criteriaRepository.findAllByLegalIssue_CaseEntity_CaseIdOrderByCreatedAtAsc(caseId);

        if (criteriaList == null || criteriaList.isEmpty()) {
            throw new CustomException(ErrorCode.CRITERIA_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();

        // 각 법리기준 AI로 평가 및 저장
        for (CriteriaEntity c : criteriaList) {
            // 이미 SATISFIED인 경우에는 재평가로 상태를 변경하지 않음
            CriteriaEvaluationEntity latestEvaluation =
                    criteriaEvaluationRepository
                            .findByCriteria_CriteriaIdOrderByCreatedAtDesc(c.getCriteriaId())
                            .stream()
                            .findFirst()
                            .orElse(null);

            if (latestEvaluation != null
                    && latestEvaluation.getStatus() == CriteriaEvaluationStatus.SATISFIED) {
                log.info("법리기준 충족평가 (Re) 생략됨 (already SATISFIED): criteriaId={}", c.getCriteriaId());
                continue;
            }
            LegalIssueEntity issueEntity = c.getLegalIssue();

            LegalIssue aiIssue = LegalIssue.builder()
                    .issueName(issueEntity == null ? null : issueEntity.getIssueName())
                    .relatedLaw(issueEntity == null ? null : issueEntity.getRelatedLaw())
                    .build();

            LegalCriteria aiCriteria = LegalCriteria.builder()
                    .criterionText(c.getCriterionText())
                    .precedentCaseNo(c.getPrecedentCaseNo())
                    .precedentDecisionDate(c.getPrecedentDecisionDate())
                    .legalIssue(aiIssue)
                    .build();

            CriteriaEvaluationRequest req = CriteriaEvaluationRequest.builder()
                    .analysis(aiAnalysis)
                    .legalCriteria(List.of(aiCriteria))
                    .build();

            CriteriaEvaluationResponse resp = aiClient.evaluateLegalCriteria(req);

            if (resp == null || resp.getStatus() == null) {
                throw new CustomException(ErrorCode.EVALUATION_STATUS_NOT_FOUNT);
            }

            log.info("Criteria Re-Evaluation: Status={}", resp.getStatus());

            CriteriaEvaluationStatus status;
            try {
                String raw = resp.getStatus().trim().toUpperCase();
                status = CriteriaEvaluationStatus.valueOf(raw);
            } catch (Exception e) {
                throw new RuntimeException("알 수 없는 status");
            }

            CriteriaEvaluationEntity evaluation = CriteriaEvaluationEntity.builder()
                    .criteria(c)
                    .status(status)
                    .reason(resp.getReason() == null ? "" : resp.getReason())
                    .evidenceGap(resp.getEvidenceGap())
                    .createdAt(now)
                    .build();

            criteriaEvaluationRepository.save(evaluation);
        }
    }

    private List<String> splitEvidenceGap(String evidenceGap) {
        if (evidenceGap == null || evidenceGap.isBlank()) {
            return null;
        }
        return List.of(evidenceGap.split("\n"));
    }
}
