package com.aivle.big_project.analysis.entity;

import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnalysisEntity {

    @Id
    @GeneratedValue
    @Column(name = "analysis_id")
    private UUID analysisId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false, unique = true)
    private InvestigationFileEntity file;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnalysisStatus status;

    @Setter
    @Column(name = "result_data", columnDefinition = "json")
    private String resultData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}