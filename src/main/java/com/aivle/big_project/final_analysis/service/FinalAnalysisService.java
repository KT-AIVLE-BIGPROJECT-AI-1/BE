package com.aivle.big_project.final_analysis.service;

import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.repository.CaseRepository;
import com.aivle.big_project.final_analysis.dto.FinalAnalysisResponse;
import com.aivle.big_project.final_analysis.entity.FinalAnalysis;
import com.aivle.big_project.final_analysis.entity.RecommendedDecision;
import com.aivle.big_project.final_analysis.repository.FinalAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinalAnalysisService {

    private final CaseRepository caseRepository;
    private final FinalAnalysisRepository finalAnalysisRepository;
//    private final AiFinalAnalysisClient aiFinalAnalysisClient;

    @Transactional
    public FinalAnalysisResponse create(UUID caseId, UUID userId) {
        CaseEntity aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("사건이 없습니다."));
        assertAssignee(aCase, userId);

        // case_detail + analysis + legal_issue + legal_criteria + criteria_eval + chat_logs 를 조회하여
        // AI 서버 요청 payload를 구성하는 로직을 붙이면 됩니다.
        // (현재는 테이블 기준 DTO/Service/Controller 우선 구현)

        // AI 호출
//        AiFinalAnalysisResponse ai = aiFinalAnalysisClient.generateFinalAnalysis(caseId);

        FinalAnalysis saved = finalAnalysisRepository.save(
                FinalAnalysis.builder()
                        .aCase(aCase)
//                        .recommendedDecision(parseDecision(ai.getRecommendedDecision()))
//                        .decisionSummary(ai.getDecisionSummary(), "decision_summary")
//                        .legalIssuesSummary(ai.getLegalIssuesSummary(), "legal_issues_summary")
//                        .evidenceSufficiencySummary(ai.getEvidenceSufficiencySummary(), "evidence_sufficiency_summary")
//                        .remainingRiskSummary(ai.getRemainingRiskSummary(), "remaining_risk_summary")
                        .build()
        );

        return toResponse(saved);
    }

    private RecommendedDecision parseDecision(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RuntimeException("recommended_decision이 비어있습니다.");
        }
        try {
            return RecommendedDecision.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("recommended_decision 값이 올바르지 않습니다: " + raw);
        }
    }

    private void assertAssignee(CaseEntity aCase, UUID userId) {
        if (aCase.getUser() == null || aCase.getUser().getUserId() == null) {
            throw new RuntimeException("사건 담당자 정보가 없습니다.");
        }
        if (!aCase.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("사건 담당자만 접근할 수 있습니다.");
        }
    }

    private FinalAnalysisResponse toResponse(FinalAnalysis fa) {
        return FinalAnalysisResponse.builder()
                .finalId(fa.getFinalId())
                .caseId(fa.getACase().getCaseId())
                .recommendedDecision(fa.getRecommendedDecision().name())
                .decisionSummary(fa.getDecisionSummary())
                .legalIssuesSummary(fa.getLegalIssuesSummary())
                .evidenceSufficiencySummary(fa.getEvidenceSufficiencySummary())
                .remainingRiskSummary(fa.getRemainingRiskSummary())
                .createdAt(fa.getCreatedAt())
                .build();
    }
}
