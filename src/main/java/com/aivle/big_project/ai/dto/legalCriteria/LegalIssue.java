package com.aivle.big_project.ai.dto.legalCriteria;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LegalIssue {
    private String issueName;
    private String relatedLaw;
    private List<LegalCriteria> legalCriteria;
}