package com.aivle.big_project.report.entity;

import com.aivle.big_project.cases.entity.Case;
import com.aivle.big_project.cases.entity.CaseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
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

    @Setter
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

    // 보고서 임시저장 업데이트
    public void updateDraft(String title, String content) {
        this.title = title;
        this.content = content;
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
