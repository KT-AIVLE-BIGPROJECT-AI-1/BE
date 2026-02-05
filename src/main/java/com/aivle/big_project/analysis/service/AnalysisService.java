package com.aivle.big_project.analysis.service;

import com.aivle.big_project.analysis.dto.*;
import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.entity.AnalysisStatus;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.keyStatement.repository.KeyStatementRepository;
import com.aivle.big_project.videoEvent.repository.VideoEventRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import com.aivle.big_project.investigationFile.entity.FileType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final KeyStatementRepository keyStatementRepository;
    private final VideoEventRepository videoEventRepository;

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

    // 분석 상세 조회
    public AnalysisDetailResponse getAnalysisDetail(UUID analysisId) {
        AnalysisEntity analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_NOT_FOUND));

        if (analysis.getStatus() != AnalysisStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ANALYSIS_NOT_COMPLETED);
        }

        FileType fileType = analysis.getFile().getFileType();

        return switch (fileType) {
            case DOCUMENT, IMAGE -> buildDocumentImage(analysis);
            case AUDIO -> buildAudio(analysis);
            case VIDEO -> buildVideo(analysis);
        };
    }

    private AnalysisDetailResponse buildDocumentImage(AnalysisEntity analysis) {
        return new AnalysisDetailResponse(
                analysis.getAnalysisId(),
                analysis.getFile().getFileType().name(),
                new DocumentImageAnalysisData(analysis.getResultData())
        );
    }

    private AnalysisDetailResponse buildAudio(AnalysisEntity analysis) {
        List<KeyStatementResponse> statements =
                keyStatementRepository.findByAnalysisId(analysis.getAnalysisId())
                        .stream()
                        .map(s -> new KeyStatementResponse(
                                s.getStatementId(),
                                s.getContent(),
                                s.getDescription(),
                                s.getCreatedAt()
                        ))
                        .toList();

        return new AnalysisDetailResponse(
                analysis.getAnalysisId(),
                analysis.getFile().getFileType().name(),
                new AudioAnalysisData(
                        analysis.getResultData(),
                        statements
                )
        );
    }

    private AnalysisDetailResponse buildVideo(AnalysisEntity analysis) {
        List<VideoEventResponse> events =
                videoEventRepository.findByAnalysisId(analysis.getAnalysisId())
                        .stream()
                        .map(e -> new VideoEventResponse(
                                e.getEventId(),
                                e.getStartTime(),
                                e.getEndTime(),
                                e.getEventType(),
                                e.getEventDescription(),
                                e.getCreatedAt()
                        ))
                        .toList();

        return new AnalysisDetailResponse(
                analysis.getAnalysisId(),
                analysis.getFile().getFileType().name(),
                new VideoAnalysisData(
                        analysis.getResultData(),
                        events
                )
        );
    }
}