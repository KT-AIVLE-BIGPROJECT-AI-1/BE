package com.aivle.big_project.videoEvent.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "video_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VideoEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "analysis_id", nullable = false)
    private UUID analysisId;

    @Column(name = "start_time", nullable = false)
    private float startTime;

    @Column(name = "end_time", nullable = false)
    private float endTime;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_description")
    private String eventDescription;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}