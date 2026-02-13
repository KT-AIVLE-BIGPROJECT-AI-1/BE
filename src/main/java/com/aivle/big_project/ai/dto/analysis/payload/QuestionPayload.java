package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter @Setter @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuestionPayload {
    private String tempId; //questionId
    private String contradictionId; //contradiction_temp_id
    private String target;
    private String question;
    private String rationale;
}
