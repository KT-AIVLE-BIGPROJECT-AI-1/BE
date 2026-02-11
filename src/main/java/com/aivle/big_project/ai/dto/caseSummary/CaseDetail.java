package com.aivle.big_project.ai.dto.caseSummary;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CaseDetail {
    private UUID caseId;
    private String title;
    private String description;
    private CaseSummary summary;
    private String type;
    private String status;
    private LocalDateTime occurredAt;
    private String location;
}
