package com.aivle.big_project.final_analysis.service;

import com.aivle.big_project.ai.AiClient;
import com.aivle.big_project.ai.dto.analysis.AiCallbackRequest;
import com.aivle.big_project.ai.dto.analysis.Analysis;
import com.aivle.big_project.ai.dto.caseSummary.CaseDetail;
import com.aivle.big_project.ai.dto.caseSummary.CaseSummary;
import com.aivle.big_project.ai.dto.chat.ChatLog;
import com.aivle.big_project.ai.dto.finalAnalysis.AiFinalAnalysisResponse;
import com.aivle.big_project.ai.dto.finalAnalysis.FinalAnalysisRequest;
import com.aivle.big_project.ai.dto.legalCriteria.CriteriaEvaluation;
import com.aivle.big_project.ai.dto.legalCriteria.LegalCriteria;
import com.aivle.big_project.ai.dto.legalCriteria.LegalIssue;
import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.repository.CaseRepository;
import com.aivle.big_project.chat_log.entity.ChatLogEntity;
import com.aivle.big_project.chat_log.repository.ChatLogRepository;
import com.aivle.big_project.criteria.entity.CriteriaEntity;
import com.aivle.big_project.criteria.entity.CriteriaEvaluationEntity;
import com.aivle.big_project.criteria.entity.LegalIssueEntity;
import com.aivle.big_project.criteria.repository.CriteriaEvaluationRepository;
import com.aivle.big_project.criteria.repository.CriteriaRepository;
import com.aivle.big_project.final_analysis.dto.FinalAnalysisResponse;
import com.aivle.big_project.final_analysis.entity.FinalAnalysis;
import com.aivle.big_project.final_analysis.entity.RecommendedDecision;
import com.aivle.big_project.final_analysis.repository.FinalAnalysisRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional
public class FinalAnalysisService {

    private final CaseRepository caseRepository;
    private final FinalAnalysisRepository finalAnalysisRepository;
    private final CriteriaRepository criteriaRepository;
    private final CriteriaEvaluationRepository evaluationRepository;
    private final AnalysisRepository analysisRepository;
    private final ChatLogRepository chatLogRepository;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public FinalAnalysisResponse create(UUID caseId, UUID userId) {

        CaseEntity aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("사건이 없습니다."));
        assertAssignee(aCase, userId); // 사건 및 담당자 검증

        CaseSummary summaryDto = parseAndNormalizeCaseSummary(aCase);

        CaseDetail caseDetail = CaseDetail.builder()
                .caseId(caseId)
                .title(aCase.getTitle())
                .description(aCase.getDescription())
                .summary(summaryDto)
                .type(aCase.getType().name())
                .status(aCase.getStatus().name())
                .occurredAt(aCase.getOccurredAt())
                .location(aCase.getLocation())
                .build();

        List<LegalCriteria> legalCriteriaList = loadLegalCriteriaList(caseId);
        List<Analysis> analysisList = loadAnalysisList(caseId);
        List<ChatLog> chatLogList = loadChatLogList(caseId);

        FinalAnalysisRequest request = FinalAnalysisRequest.builder()
                .caseDetail(caseDetail)
                .legalCriteria(legalCriteriaList)
                .analyses(analysisList)
                .chatLogs(chatLogList)
                .build();

        log.info("Final Analysis Request Created : legal_criteria={}, analyses={}, chat_logs={}", legalCriteriaList.size(), analysisList.size(), chatLogList.size());
        if( legalCriteriaList.isEmpty() || analysisList.isEmpty()) System.out.println("criteria or analysis is empty");

        AiFinalAnalysisResponse response = aiClient.createFinalAnalysis(request);
        log.info("Final Analysis Response Received : " + response.toString());

        FinalAnalysis saved = finalAnalysisRepository.save(
                FinalAnalysis.builder()
                        .aCase(aCase)
                        .recommendedDecision(parseDecision(response.getRecommendedDecision()))
                        .decisionSummary(response.getDecisionSummary())
                        .legalIssuesSummary(response.getLegalIssuesSummary())
                        .evidenceSufficiencySummary(response.getEvidenceSufficiencySummary())
                        .remainingRiskSummary(response.getRemainingRiskSummary())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        return toResponse(saved);
    }

    private List<LegalCriteria> loadLegalCriteriaList(UUID caseId) {
        List<CriteriaEntity> criteriaEntities =
                criteriaRepository.findAllByLegalIssue_CaseEntity_CaseIdOrderByCreatedAtAsc(caseId);

        if (criteriaEntities == null || criteriaEntities.isEmpty()) {
            return List.of();
        }

        // 동일 legal_issue 중복 제거
        Map<UUID, com.aivle.big_project.ai.dto.legalCriteria.LegalIssue> issueCache = new HashMap<>();

        List<LegalCriteria> result = new ArrayList<>(criteriaEntities.size());
        int evalPresent = 0;
        int evalDefaulted = 0;

        for (CriteriaEntity c : criteriaEntities) {
            LegalIssueEntity issueEntity = c.getLegalIssue();

            com.aivle.big_project.ai.dto.legalCriteria.LegalIssue aiIssue = null;
            if (issueEntity != null) {
                UUID issueId = issueEntity.getIssueId();
                aiIssue = issueCache.get(issueId);
                if (aiIssue == null) {
                    aiIssue = com.aivle.big_project.ai.dto.legalCriteria.LegalIssue.builder()
                            .issueName(issueEntity.getIssueName())
                            .relatedLaw(issueEntity.getRelatedLaw())
                            .build();
                    issueCache.put(issueId, aiIssue);
                }
            }

            // 최신 평가 1건만 조회
            CriteriaEvaluationEntity latestEval = evaluationRepository
                    .findByCriteria_CriteriaIdOrderByCreatedAtDesc(c.getCriteriaId())
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (latestEval != null) evalPresent++;
            else evalDefaulted++;

            CriteriaEvaluation aiEval;
            if(latestEval != null) {
                aiEval = CriteriaEvaluation.builder()
                        .status(latestEval.getStatus() == null ? null : latestEval.getStatus().name())
                        .reason(latestEval.getReason())
                        .evidenceGap(latestEval.getEvidenceGap())
                        .build();
            } else {
                // CriteriaEvaluation이 내부 오류로 존재하지 않는 경우 : AI 서버 500 에러 방지를 위해 기본값 반환
                aiEval = CriteriaEvaluation.builder()
                        .status("INSUFFICIENT")
                        .reason(null)
                        .evidenceGap(null)
                        .build();
            }
            LegalCriteria aiCriteria = LegalCriteria.builder()
                    .criterionText(c.getCriterionText())
                    .precedentCaseNo(c.getPrecedentCaseNo())
                    .precedentDecisionDate(c.getPrecedentDecisionDate())
                    .legalIssue(aiIssue)
                    .criteriaEvaluation(aiEval)
                    .build();

            result.add(aiCriteria);
        }
        log.info("FinalAnalysis legalCriteria mapped: total={}, evalPresent={}, evalDefaulted={}", result.size(), evalPresent, evalDefaulted);
        return result;
    }

    private List<Analysis> loadAnalysisList(UUID caseId) {
        List<AnalysisEntity> analysisEntityList = analysisRepository.findAllByFile_CaseIdOrderByCreatedAtAsc(caseId);

        if (analysisEntityList == null || analysisEntityList.isEmpty()) return List.of();

        List<Analysis> result = new ArrayList<>(analysisEntityList.size());

        for (AnalysisEntity analysis : analysisEntityList) {
            InvestigationFileEntity file = analysis.getFile();

            JsonNode aggregated = null;
            String resultString = analysis.getResultData();

            if (resultString != null && !resultString.isBlank()) {
                try {
                    aggregated = objectMapper.readTree(resultString);
                } catch (Exception e) {
                    throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
                }
            }
            Analysis dto = Analysis.builder()
                    .analysisId(analysis.getAnalysisId())
                    .fileType(file == null || file.getFileType() == null ? null : file.getFileType().name())
                    .status(analysis.getStatus() == null ? null : analysis.getStatus().name())
                    .resultData(aggregated)
                    .build();
            result.add(dto);
        }
        return result;
    }

    private List<ChatLog> loadChatLogList(UUID caseId) {
        List<ChatLogEntity> chatLogEntityList = chatLogRepository.findAllByCaseEntity_CaseIdOrderByCreatedAtAsc(caseId);

        if (chatLogEntityList == null || chatLogEntityList.isEmpty()) {
            return List.of();
        }

        List<ChatLog> result = new ArrayList<>(chatLogEntityList.size());

        for (ChatLogEntity chatLog : chatLogEntityList) {
            ChatLog dto = ChatLog.builder()
                    .role(chatLog.getRole())
                    .message(chatLog.getMessage())
                    .usedContext(chatLog.getUsedContext())
                    .createdAt(chatLog.getCreatedAt())
                    .build();
            result.add(dto);
        }
        return result;
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

    private CaseSummary parseAndNormalizeCaseSummary(CaseEntity aCase) {
        CaseSummary summaryDto = null;
        try {
            // CaseEntity.summary가 JSON 문자열이면 CaseSummary로 파싱
            if (aCase.getSummary() != null && !aCase.getSummary().isBlank()) {
                summaryDto = objectMapper.readValue(aCase.getSummary(), CaseSummary.class);
            }
        } catch (Exception ignored) {
            // summary 파싱 실패 시 null 유지
        }
        return summaryDto;
    }

    private FinalAnalysisResponse toResponse(FinalAnalysis fa) {
        return FinalAnalysisResponse.builder()
                .finalId(fa.getFinalId())
                .caseId(fa.getACase().getCaseId())
                .recommendedDecision(
                        fa.getRecommendedDecision() != null
                                ? fa.getRecommendedDecision().name()
                                : null
                )
                .decisionSummary(fa.getDecisionSummary())
                .legalIssuesSummary(fa.getLegalIssuesSummary())
                .evidenceSufficiencySummary(fa.getEvidenceSufficiencySummary())
                .remainingRiskSummary(fa.getRemainingRiskSummary())
                .createdAt(fa.getCreatedAt())
                .build();
    }
}
