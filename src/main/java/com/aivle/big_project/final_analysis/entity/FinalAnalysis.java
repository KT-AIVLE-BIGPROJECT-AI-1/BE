package com.aivle.big_project.final_analysis.entity;

import com.aivle.big_project.cases.entity.Case;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;


@Getter @NoArgsConstructor
@Builder @AllArgsConstructor
@Entity @Table(name = "REPORT")
public class FinalAnalysis {
    @Id @UuidGenerator
    @Column(name = "final_id", columnDefinition = "uuid")
    private UUID finalId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "case_id", nullable = false)
    private Case aCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommended_decision", nullable = false, length = 30)
    private RecommendedDecision recommendedDecision;

    @Column(name = "decision_summary", columnDefinition = "text", nullable = false)
    private String decisionSummary;

    @Column(name = "legal_issues_summary", columnDefinition = "text", nullable = false)
    private String legalIssuesSummary;

    @Column(name = "evidence_sufficiency_summary", columnDefinition = "text", nullable = false)
    private String evidenceSufficiencySummary;

    @Column(name = "remaining_risk_summary", columnDefinition = "text", nullable = false)
    private String remainingRiskSummary;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}
