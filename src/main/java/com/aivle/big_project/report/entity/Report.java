package com.aivle.big_project.report.entity;

import com.aivle.big_project.cases.entity.Case;
import com.aivle.big_project.cases.entity.CaseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @NoArgsConstructor
@Builder @AllArgsConstructor
@Entity @Table(name = "REPORT")
public class Report {

    @Id @UuidGenerator
    @Column(name = "report_id", nullable = false)
    private UUID reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case aCase;

    @Column(name = "final_id")
    private UUID finalId;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_path", columnDefinition = "TEXT")
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // 보고서 임시저장
    public static Report newDraft(Case aCase, ReportType type, String title, String content, UUID finalId) {
        return Report.builder()
                .reportId(UUID.randomUUID())
                .aCase(aCase)
                .type(type)
                .status(ReportStatus.DRAFT)
                .title(title)
                .content(content)
                .finalId(finalId)
                .filePath(null)
                .build();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (status == null) status = ReportStatus.DRAFT;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void finalizeReport(String filePath) {
        this.status = ReportStatus.FINALIZED;
        this.filePath = filePath;
    }

}
