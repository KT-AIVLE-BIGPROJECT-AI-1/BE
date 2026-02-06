package com.aivle.big_project.report.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportDetailResponse {

    private UUID reportId;
    private UUID caseId;
    private UUID finalId;

    private String type; // ReportType
    private String status; // ReportStatus

    private String title;
    private String content;
    private String filePath;

    private List<UUID> selectedLogIds; // INTERIM만 채움
    private Instant createdAt;
    private Instant updatedAt;
}