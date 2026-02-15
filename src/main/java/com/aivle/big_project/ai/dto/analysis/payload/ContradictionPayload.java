package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContradictionPayload {
    private String tempId; //contradictionId
    private String statementId1; //keyStatement-1
    private String statementId2; //keyStatement-2
    private String description;
    private String type;
}
