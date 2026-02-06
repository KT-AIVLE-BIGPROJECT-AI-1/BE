package com.aivle.big_project.transcript.dto;

import com.aivle.big_project.transcript.entity.TranscriptEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TranscriptItem {

    private UUID transcriptId;
    private String speaker;
    private String text;
    private LocalDateTime createdAt;

    public static TranscriptItem from(TranscriptEntity transcript) {
        return new TranscriptItem(
                transcript.getTranscriptId(),
                transcript.getSpeaker(),
                transcript.getText(),
                transcript.getCreatedAt()
        );
    }
}