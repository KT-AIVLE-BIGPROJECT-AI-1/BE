package com.aivle.big_project.cases.service;

import com.aivle.big_project.cases.dto.TabFlags;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaseTabFlagService {

    // 모순, 자료파일, 보고서 생성 이후에 적용
//    private final ContradictionRepository contradictionRepository;
//    private final CaseFileRepository caseFileRepository;
//    private final ReportRepository reportRepository;

    public TabFlags getFlags(UUID caseId) {
        boolean hasContradictions = false; // contradictionRepository..existsByCase_CaseId(caseId);
        boolean hasVideoFiles = false; // caseFileRepository.existsByCase_CaseIdAndFileType(caseId, FileType.VIDEO);
        boolean hasReports = false; // reportRepository.existsByCase_CaseId(caseId);

        return TabFlags.builder()
                .hasContradictions(hasContradictions)
                .hasVideoFiles(hasVideoFiles)
                .hasReports(hasReports)
                .build();
    }
}
