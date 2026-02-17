package com.aivle.big_project.transcript.service;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.entity.AnalysisStatus;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import com.aivle.big_project.investigationFile.entity.FileType;
import com.aivle.big_project.transcript.dto.TranscriptResponse;
import com.aivle.big_project.transcript.entity.TranscriptEntity;
import com.aivle.big_project.transcript.repository.TranscriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TranscriptService {

    private final AnalysisRepository analysisRepository;
    private final TranscriptRepository transcriptRepository;

    public TranscriptResponse getTranscripts(UUID analysisId) {

        AnalysisEntity analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_NOT_FOUND));

        // 분석 완료 상태 체크
        if (analysis.getStatus() != AnalysisStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ANALYSIS_NOT_COMPLETED);
        }

        // 자료 타입 AUDIO 체크
        if (analysis.getFile().getFileType() != FileType.AUDIO) {
            throw new CustomException(ErrorCode.NOT_AUDIO_ANALYSIS);
        }

        List<TranscriptEntity> transcripts =
                transcriptRepository.findAllByAnalysis_AnalysisIdOrderByCreatedAtAsc(analysisId);

        return TranscriptResponse.from(analysisId, transcripts);
    }
}