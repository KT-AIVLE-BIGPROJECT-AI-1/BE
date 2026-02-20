package com.aivle.big_project.analysis.controller;

import com.aivle.big_project.ai.dto.analysis.AiCallbackRequest;
import com.aivle.big_project.analysis.dto.AnalysisDetailResponse;
import com.aivle.big_project.analysis.dto.AnalysisStatusResponse;
import com.aivle.big_project.analysis.service.AnalysisService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cases/{caseId}/analysis/{analysisId}")
public class AnalysisController {

    private final AnalysisService analysisService;

    // 분석 상태 조회
    @GetMapping("/status")
    public AnalysisStatusResponse getStatus(
            @PathVariable UUID caseId,
            @PathVariable UUID analysisId
    ) {
        return analysisService.getStatus(analysisId);
    }

    // 분석 상세 조회
    @GetMapping
    public AnalysisDetailResponse getAnalysisDetail(
            @PathVariable UUID caseId,
            @PathVariable UUID analysisId
    ) {
        return analysisService.getAnalysisDetail(analysisId);
    }

    // 파일 분석 CallBack
    @PostMapping("/callback")
    public ResponseEntity<?> fileAnalysisCallBack(
            @PathVariable UUID caseId,
            @PathVariable UUID analysisId,
            @RequestBody AiCallbackRequest request
    ) {
        System.out.println(request.toString());
        analysisService.handleStageCallback(caseId, analysisId, request);
        return ResponseEntity.ok(Map.of("message", "Callback Received"));
    }
}