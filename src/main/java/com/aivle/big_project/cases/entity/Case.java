package com.aivle.big_project.cases.entity;

import com.aivle.big_project.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Builder
@Table(name = "CASES")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Case {

    @Id @UuidGenerator
    @Column(name = "case_id", columnDefinition = "uuid")
    private UUID caseId;

    // 사용자ID(user_id) - 사건 담당자 (현재 스키마상 owner/assignee 구분이 없음)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "uuid")
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String description; // 사건 개요 (담당자 입력)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String summary; // AI 구조화 요약 (JSON 문자열)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CaseType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CaseStatus status;

    @Column(name = "occured_at")
    private LocalDateTime occuredAt;

    @Column(length = 225)
    private String location;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) this.status = CaseStatus.OPEN;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateSummary(String summaryJson) {
        this.summary = summaryJson;
    }

    public void updateStatus(CaseStatus status) {
        this.status = status;
    }

    public void updateUser(User user) {
        this.user = user;
    }
}
