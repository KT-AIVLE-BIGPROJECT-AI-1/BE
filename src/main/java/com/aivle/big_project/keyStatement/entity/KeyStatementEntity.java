package com.aivle.big_project.keyStatement.entity;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.cases.entity.CaseStatus;
import com.aivle.big_project.transcript.entity.TranscriptEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "key_statement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class KeyStatementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "statement_id")
    private UUID statementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private AnalysisEntity analysis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transcript_id", nullable = false)
    private TranscriptEntity transcript;

    @Column(name = "statement_text", nullable = false)
    private String statementText;

    @Column(name = "description")
    private String description;

    @Column(name = "temp_id")
    private String tempId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}