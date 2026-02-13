package com.aivle.big_project.ai.dto.legalCriteria;

import com.aivle.big_project.ai.dto.caseSummary.CaseSummary;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LegalCriteriaRequest {
    private String description;
    private CaseSummary summary;
    private String type;
    private LocalDateTime occurredAt;
    private String location;
}
