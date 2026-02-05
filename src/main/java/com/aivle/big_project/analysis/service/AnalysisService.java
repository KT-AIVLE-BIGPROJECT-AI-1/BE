package com.aivle.big_project.analysis.service;

import com.aivle.big_project.analysis.dto.AnalysisStatusResponse;
import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final AnalysisRepository analysisRepository;

    // 분석 상태 조회
    public AnalysisStatusResponse getStatus(UUID analysisId) {
        AnalysisEntity analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.ANALYSIS_NOT_FOUND)
                );

        return new AnalysisStatusResponse(
                analysis.getAnalysisId(),
                analysis.getStatus()
        );
    }
}