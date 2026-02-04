package com.aivle.big_project.activity_log.entity;

import com.aivle.big_project.cases.entity.Case;
import com.aivle.big_project.report.entity.Report;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter @NoArgsConstructor
@Builder @AllArgsConstructor
@Entity @Table(name = "ACTIVITY_LOG")
public class ActivityLog {

    @Id @UuidGenerator
    @Column(name = "log_id", nullable = false)
    private UUID logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case aCase;

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

    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ActivityLogStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public void record(ActivityLogField field, ActivityType activityType, String payload) {
        this.field = field;
        this.activityType = activityType;
        this.payload = payload;
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
