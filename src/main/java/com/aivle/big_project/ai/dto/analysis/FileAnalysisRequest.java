package com.aivle.big_project.ai.dto.analysis;

import com.aivle.big_project.ai.dto.caseSummary.CaseDetail;
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
public class FileAnalysisRequest {
    private CaseDetail caseDetail;
    private List<InvestigationFile> investigationFiles;
    private List<LegalCriteria> legalCriteria;
}
