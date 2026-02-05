package com.aivle.big_project.criteria.service;

import com.aivle.big_project.criteria.entity.CriteriaEvaluationStatus;
import com.aivle.big_project.criteria.dto.CriteriaDetailResponse;
import com.aivle.big_project.criteria.dto.CriteriaListResponse;
import com.aivle.big_project.criteria.entity.CriteriaEntity;
import com.aivle.big_project.criteria.entity.CriteriaEvaluationEntity;
import com.aivle.big_project.criteria.entity.LegalIssueEntity;
import com.aivle.big_project.criteria.repository.CriteriaEvaluationRepository;
import com.aivle.big_project.criteria.repository.CriteriaRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CriteriaService {

    private final CriteriaRepository criteriaRepository;
    private final CriteriaEvaluationRepository criteriaEvaluationRepository;

    //체크리스트(법리 판단 기준) 목록 조회
    public CriteriaListResponse getCriteriaList(UUID caseId) {
        return new CriteriaListResponse(
                criteriaRepository.findCriteriaItemsByCaseId(caseId)
        );
    }

    //체크리스트 상세 조회
    public CriteriaDetailResponse getCriteriaDetail(
            UUID caseId,
            UUID criteriaId,
            String evaluationStatus
    ) {
        // 1️⃣ criteria가 해당 case에 속하는지 검증
        CriteriaEntity criteria = criteriaRepository
                .findByCaseIdAndCriteriaId(caseId, criteriaId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 법리 판단 기준입니다."));

        // 2️⃣ 쟁점 조회
        LegalIssueEntity issue = criteria.getLegalIssue();

        // 3️⃣ 평가 조회
        CriteriaEvaluationEntity evaluation = criteriaEvaluationRepository
                .findByCriteria_CriteriaId(criteriaId)
                .orElseThrow(() -> new IllegalArgumentException("평가 정보가 존재하지 않습니다."));

        // 4️⃣ 상태 분기
        CriteriaEvaluationStatus requestStatus =
                CriteriaEvaluationStatus.valueOf(evaluationStatus);

        boolean satisfied = requestStatus == CriteriaEvaluationStatus.SATISFIED;

        // 5️⃣ DTO 조립
        return CriteriaDetailResponse.builder()
                .criteriaId(criteria.getCriteriaId())
                .criterionText(criteria.getCriterionText())
                .issue(new CriteriaDetailResponse.IssueInfo(
                        issue.getIssueName(),
                        issue.getRelatedLaw()
                ))
                .precedent(new CriteriaDetailResponse.PrecedentInfo(
                        criteria.getPrecedentCaseNo()
                ))
                .evaluation(
                        CriteriaDetailResponse.EvaluationInfo.builder()
                                .status(requestStatus.name())
                                .reason(evaluation.getReason())
                                .evidenceSupplementPoints(
                                        satisfied ? null : splitEvidenceGap(evaluation.getEvidenceGap())
                                )
                                .build()
                )
                .build();
    }

    private List<String> splitEvidenceGap(String evidenceGap) {
        if (evidenceGap == null || evidenceGap.isBlank()) {
            return List.of();
        }
        return List.of(evidenceGap.split("\n"));
    }

    @Transactional
    public void deleteCriteria(UUID caseId, UUID criteriaId) {

        CriteriaEntity criteria = criteriaRepository
                .findByCaseIdAndCriteriaId(caseId, criteriaId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.CRITERIA_NOT_FOUND)
                );

        // 분석/평가 결과가 존재하면 삭제 불가
        if (criteriaEvaluationRepository.existsByCriteria_CriteriaId(criteriaId)) {
            throw new CustomException(
                    ErrorCode.CRITERIA_ALREADY_ANALYZED
            );
        }

        criteriaRepository.delete(criteria);
    }
}
