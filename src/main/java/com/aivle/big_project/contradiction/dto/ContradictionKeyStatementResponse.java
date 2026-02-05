package com.aivle.big_project.contradiction.dto;

import com.aivle.big_project.keyStatement.entity.KeyStatementEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
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