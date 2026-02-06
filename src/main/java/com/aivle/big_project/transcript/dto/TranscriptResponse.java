package com.aivle.big_project.transcript.dto;

import com.aivle.big_project.transcript.entity.TranscriptEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@Getter
@AllArgsConstructor
public class TranscriptResponse {

    private UUID analysisId;
    private List<TranscriptItem> transcripts;

    public static TranscriptResponse from(UUID analysisId, List<TranscriptEntity> transcripts) {
        return new TranscriptResponse(
                analysisId,
                transcripts.stream()
                        .map(TranscriptItem::from)
                        .toList()
        );
    }
}