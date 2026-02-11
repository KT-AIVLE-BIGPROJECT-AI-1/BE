package com.aivle.big_project.ai.dto.caseSummary;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CaseAiSummaryRequest {
    private String title;
    private String description;
    private String type;
    private LocalDateTime occurred_at;
    private String location;
}
