package com.aivle.big_project.contradiction.entity;

import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.entity.CaseStatus;
import com.aivle.big_project.keyStatement.entity.KeyStatementEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "contradiction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ContradictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "contradiction_id")
    private UUID contradictionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseEntity caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id_1", nullable = false)
    private KeyStatementEntity statement1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id_2", nullable = false)
    private KeyStatementEntity statement2;

    // AI 분석 결과 라벨 그대로 저장 (확장을 위해 enum 지양)
    @Column(name = "type", nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContradictionStatus status;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void changeStatus(ContradictionStatus targetStatus) {
        if (!this.status.canChangeTo(targetStatus)) {
            throw new IllegalStateException("INVALID_CONTRADICTION_STATUS_TRANSITION");
        }
        this.status = targetStatus;
    }
}