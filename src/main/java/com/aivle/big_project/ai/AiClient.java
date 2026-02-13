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

public interface AiClient {
    // 사건 구조화 요약
    CaseAiSummaryResponse createCaseSummary(CaseAiSummaryRequest request);
    // 법리쟁점|법리판단기준|(초기)법리기준 충족평가 생성
    LegalCriteriaResponse createLegalCriteria(LegalCriteriaRequest request);
    // (자료 분석에 따른) 법리기준 충족평가
    CriteriaEvaluationResponse evaluateLegalCriteria(CriteriaEvaluationRequest request);
    // 수사 자료 분석
    FileAnalysisResponse uploadInvestigationFile(FileAnalysisRequest request);
    // 최종 통합분석 생성
    AiFinalAnalysisResponse createFinalAnalysis(FinalAnalysisRequest request);
    // '수사보고' 초안 생성
    InterimReportCreateResponse createInterimReport(InterimReportCreateRequest request);
    // '수사결과보고' 초안 생성
    FinalReportCreateResponse createFinalReport(FinalReportCreateRequest request);
    // '수사 결정서 (송치|불송치|수사중지) 초안 생성
    DecisionReportCreateResponse createDecisionReport(DecisionReportCreateRequest request);
    // 챗 응답 생성 요청
    ChatCreateResponse respondChat(ChatCreateRequest request);
}
