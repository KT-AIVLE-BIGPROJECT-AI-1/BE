package com.aivle.big_project.ai;

import com.aivle.big_project.ai.dto.analysis.FileAnalysisRequest;
import com.aivle.big_project.ai.dto.analysis.FileAnalysisResponse;
import com.aivle.big_project.ai.dto.caseSummary.CaseAiSummaryRequest;
import com.aivle.big_project.ai.dto.caseSummary.CaseAiSummaryResponse;
import com.aivle.big_project.ai.dto.chat.ChatCreateRequest;
import com.aivle.big_project.ai.dto.chat.ChatCreateResponse;
import com.aivle.big_project.ai.dto.finalAnalysis.FinalAnalysisRequest;
import com.aivle.big_project.ai.dto.finalAnalysis.AiFinalAnalysisResponse;
import com.aivle.big_project.ai.dto.legalCriteria.CriteriaEvaluationRequest;
import com.aivle.big_project.ai.dto.legalCriteria.CriteriaEvaluationResponse;
import com.aivle.big_project.ai.dto.legalCriteria.LegalCriteriaRequest;
import com.aivle.big_project.ai.dto.legalCriteria.LegalCriteriaResponse;
import com.aivle.big_project.ai.dto.report.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AiWebClient implements AiClient{

    private final WebClient webClient;

    public AiWebClient(@Qualifier("aiHttpWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // 특정 경로로 요청
    private <T> T postJson(String path, Object request, Class<T> responseType) {
        return webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    @Override
    public CaseAiSummaryResponse createCaseSummary(CaseAiSummaryRequest request) {
        return postJson("/cases/summary", request, CaseAiSummaryResponse.class);
    }

    @Override
    public LegalCriteriaResponse createLegalCriteria(LegalCriteriaRequest request) {
        return postJson("/legal-criteria", request, LegalCriteriaResponse.class);
    }

    @Override
    public CriteriaEvaluationResponse evaluateLegalCriteria(CriteriaEvaluationRequest request) {
        return postJson("/legal-criteria/evaluate", request, CriteriaEvaluationResponse.class);
    }

    @Override
    public FileAnalysisResponse uploadInvestigationFile(FileAnalysisRequest request) {
        return postJson("/analysis/upload", request, FileAnalysisResponse.class);
    }

    @Override
    public AiFinalAnalysisResponse createFinalAnalysis(FinalAnalysisRequest request) {
        return postJson("/analysis/final", request, AiFinalAnalysisResponse.class);
    }

    @Override
    public InterimReportCreateResponse createInterimReport(InterimReportCreateRequest request) {
        return postJson("/reports/interim", request, InterimReportCreateResponse.class);
    }

    @Override
    public FinalReportCreateResponse createFinalReport(FinalReportCreateRequest request) {
        return postJson("/reports/final", request, FinalReportCreateResponse.class);
    }

    @Override
    public DecisionReportCreateResponse createDecisionReport(DecisionReportCreateRequest request) {
        return postJson("/reports/decision", request, DecisionReportCreateResponse.class);
    }

    @Override
    public ChatCreateResponse respondChat(ChatCreateRequest request) {
        return postJson("/chat/respond", request, ChatCreateResponse.class);
    }
}
