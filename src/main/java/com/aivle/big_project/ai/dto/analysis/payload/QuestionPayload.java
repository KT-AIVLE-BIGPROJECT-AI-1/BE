package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuestionPayload {
    private String tempId; //questionId
    private String contradictionId; //contradiction_temp_id
    private String target;
    private String question;
    private String rationale;
}
