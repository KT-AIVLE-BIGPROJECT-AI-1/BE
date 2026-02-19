package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KeyStatementPayload {
    private String tempId; //keyStatementId
    private String transcriptId; //transcript_temp_id
    private String statementText;
    private String content;
}
