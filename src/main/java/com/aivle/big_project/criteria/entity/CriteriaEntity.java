package com.aivle.big_project.criteria.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "legal_criteria")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CriteriaEntity {

    @Id
    @GeneratedValue
    @Column(name = "criteria_id", columnDefinition = "uuid")
    private UUID criteriaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private LegalIssueEntity legalIssue;

    @Column(name = "criterion_text", nullable = false, columnDefinition = "text")
    private String criterionText;

    @Column(name = "precedent_case_no", length = 50)
    private String precedentCaseNo;

    @Column(name = "precedent_decision_date")
    private LocalDate precedentDecisionDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}