package com.aivle.big_project.report.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportSummaryResponse {
    private UUID reportId;
    private UUID caseId;

    private String type;
    private String status;

    private String title;
    private String filePath;

    private LocalDateTime updatedAt;
}
