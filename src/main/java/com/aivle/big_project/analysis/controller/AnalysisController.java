package com.aivle.big_project.analysis.controller;

import com.aivle.big_project.analysis.dto.AnalysisStatusResponse;
import com.aivle.big_project.analysis.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cases/{caseId}/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    // 분석 상태 조회
    @GetMapping("/{analysisId}/status")
    public AnalysisStatusResponse getStatus(
            @PathVariable UUID caseId,
            @PathVariable UUID analysisId
    ) {
        return analysisService.getStatus(analysisId);
    }
}