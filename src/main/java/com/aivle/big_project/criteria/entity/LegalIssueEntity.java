package com.aivle.big_project.criteria.entity;

import com.aivle.big_project.cases.entity.CaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "legal_issue")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LegalIssueEntity {

    @Id
    @GeneratedValue
    @Column(name = "issue_id", columnDefinition = "uuid")
    private UUID issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseEntity caseEntity;

    @Column(name = "issue_name", nullable = false)
    private String issueName;

    @Column(name = "related_law")
    private String relatedLaw;
}