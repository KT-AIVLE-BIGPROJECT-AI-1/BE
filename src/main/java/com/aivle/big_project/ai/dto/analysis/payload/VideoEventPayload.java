package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VideoEventPayload {
    private float startTime;
    private float endTime;
    private String eventType;
    private String eventDescription;
}
