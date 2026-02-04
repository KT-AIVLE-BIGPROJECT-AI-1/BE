package com.aivle.big_project.criteria.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "criteria_evaluation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CriteriaEvaluationEntity {

    @Id
    @GeneratedValue
    @Column(name = "evaluation_id", columnDefinition = "uuid")
    private UUID evaluationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id", nullable = false)
    private CriteriaEntity criteria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CriteriaEvaluationStatus status;

    @Column(name = "reason", columnDefinition = "text")
    private String reason;

    @Column(name = "evidence_gap", columnDefinition = "text")
    private String evidenceGap;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
