package com.aivle.big_project.keyStatement.dto;

import com.aivle.big_project.keyStatement.entity.KeyStatementEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class KeyStatementResponse {

    private UUID statementId;
    private String statementText;

    public static KeyStatementResponse from(KeyStatementEntity keyStatement) {
        return new KeyStatementResponse(
                keyStatement.getStatementId(),
                keyStatement.getStatementText()
        );
    }
}