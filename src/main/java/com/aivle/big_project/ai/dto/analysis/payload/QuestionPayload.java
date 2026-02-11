package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter @Setter @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class QuestionPayload {
    private String statement_tempId;
    private String target; // optional
    private String question;
}
