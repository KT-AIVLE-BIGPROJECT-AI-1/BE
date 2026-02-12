package com.aivle.big_project.cases.service;

import com.aivle.big_project.analysis.entity.AnalysisStatus;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.cases.dto.TabFlags;
import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.entity.CaseStatus;
import com.aivle.big_project.cases.repository.CaseRepository;
import com.aivle.big_project.contradiction.repository.ContradictionRepository;
import com.aivle.big_project.final_analysis.repository.FinalAnalysisRepository;
import com.aivle.big_project.investigationFile.entity.FileType;
import com.aivle.big_project.investigationFile.repository.InvestigationFileRepository;
import com.aivle.big_project.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseTabFlagService {

    // 모순, 자료파일, 보고서 생성 이후에 적용
    private final ContradictionRepository contradictionRepository;
    private final FinalAnalysisRepository finalAnalysisRepository;
    private final AnalysisRepository analysisRepository;
    private final CaseRepository caseRepository;

    public TabFlags getFlags(UUID caseId) {

        boolean hasContradictions =
                contradictionRepository.countByCaseIdViaStatement1(caseId) > 0
                        || contradictionRepository.countByCaseIdViaStatement2(caseId) > 0;

        boolean hasVideoFiles =
                analysisRepository.existsCompletedAnalysisByCaseIdAndType(
                        caseId,
                        FileType.VIDEO,
                        AnalysisStatus.COMPLETED
                );

        boolean hasFinalAnalysis = false;
        CaseEntity caseEntity = caseRepository.findById(caseId).orElseThrow();

        if (caseEntity.getStatus() == CaseStatus.DONE) {
            hasFinalAnalysis =
                    finalAnalysisRepository.existsByACase_CaseId(caseId);
        }

        return TabFlags.builder()
                .hasContradictions(hasContradictions)
                .hasVideoFiles(hasVideoFiles)
                .hasFinalAnalysis(hasFinalAnalysis)
                .build();
    }
}
