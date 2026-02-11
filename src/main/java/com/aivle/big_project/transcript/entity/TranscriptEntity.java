package com.aivle.big_project.transcript.entity;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "transcript")
@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class TranscriptEntity {

    @Id
    @Column(name = "transcript_id")
    private UUID transcriptId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false, unique = true)
    private AnalysisEntity analysis;

    @Column(name = "speaker")
    private String speaker;

    @Column(name = "text")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
