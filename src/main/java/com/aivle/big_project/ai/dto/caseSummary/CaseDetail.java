package com.aivle.big_project.ai.dto.caseSummary;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor @ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CaseDetail {
    private UUID caseId;
    private String title;
    private String description;
    private CaseSummary summary;
    private String type;
    private String status;
    private String occurredAt;
    private String location;
}
