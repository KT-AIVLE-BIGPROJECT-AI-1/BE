package com.aivle.big_project.analysis.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class VideoEventResponse {

    private final UUID eventId;
    private final float startTime;
    private final float endTime;
    private final String eventType;
    private final String eventDescription;
    private final LocalDateTime createdAt;

    public VideoEventResponse(
            UUID eventId,
            float startTime,
            float endTime,
            String eventType,
            String eventDescription,
            LocalDateTime createdAt
    ) {
        this.eventId = eventId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
        this.eventDescription = eventDescription;
        this.createdAt = createdAt;
    }
}