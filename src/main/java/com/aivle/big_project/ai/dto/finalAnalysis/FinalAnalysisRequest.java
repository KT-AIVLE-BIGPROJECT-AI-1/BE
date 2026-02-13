package com.aivle.big_project.ai.dto.finalAnalysis;

import com.aivle.big_project.ai.dto.analysis.Analysis;
import com.aivle.big_project.ai.dto.caseSummary.CaseDetail;
import com.aivle.big_project.ai.dto.chat.ChatLog;
import com.aivle.big_project.ai.dto.legalCriteria.LegalCriteria;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FinalAnalysisRequest {
    private CaseDetail caseDetail;
    private List<LegalCriteria> legalCriteria;
    private List<Analysis> analyses;
    private List<ChatLog> chatLogs;
}
