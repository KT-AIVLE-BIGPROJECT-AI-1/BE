package com.aivle.big_project.transcript.entity;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "transcript")
@Getter
public class TranscriptEntity {

    @Id
    private UUID transcriptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private AnalysisEntity analysis;

    private String speaker;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime createdAt;
}
