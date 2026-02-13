package com.aivle.big_project.activity_log.entity;

import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.report.entity.Report;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @NoArgsConstructor
@Builder @AllArgsConstructor @Setter
@Entity @Table(name = "ACTIVITY_LOG")
public class ActivityLog {

    @Id @UuidGenerator
    @Column(name = "log_id", nullable = false)
    private UUID logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private CaseEntity caseEntity;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @Enumerated(EnumType.STRING)
    @Column(name = "field", nullable = false)
    private ActivityLogField field;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false)
    private ActivityType activityType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ActivityLogStatus status = ActivityLogStatus.NOT_INCLUDED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void attachToReport(Report report) {
        this.report = report;
        this.status = ActivityLogStatus.INCLUDED;
    }

    public void detachFromReport() {
        this.report = null;
        this.status = ActivityLogStatus.NOT_INCLUDED;
    }
}
