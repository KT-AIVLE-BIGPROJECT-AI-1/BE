package com.aivle.big_project.final_analysis.controller;


import com.aivle.big_project.final_analysis.service.FinalAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.aivle.big_project.security.SecurityUtils.currentUserId;

@RestController
@RequiredArgsConstructor
public class FinalAnalysisController {

    private final FinalAnalysisService finalAnalysisService;

    // 최종 통합분석 생성 요청
    @PostMapping("/cases/{caseId}/final-analysis")
    public ResponseEntity<?> create(@PathVariable UUID caseId) {
        UUID userId = currentUserId();
        return ResponseEntity.ok(finalAnalysisService.create(caseId, userId));
    }
}
