package com.aivle.big_project.contradiction.dto;

import com.aivle.big_project.keyStatement.entity.KeyStatementEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContradictionKeyStatementResponse {

    private UUID statementId;
    private String speaker;
    private String transcript;

    public static ContradictionKeyStatementResponse from(KeyStatementEntity entity) {
        return new ContradictionKeyStatementResponse(
                entity.getStatementId(),
                entity.getTranscript().getSpeaker(), // 🔥 발화자
                entity.getStatementText()             // 핵심 진술 문장
        );
    }
}