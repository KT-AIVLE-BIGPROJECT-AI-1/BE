package com.aivle.big_project.analysis.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class KeyStatementResponse {

    private final UUID statementId;
    private final String content;
    private final String description;
    private final LocalDateTime createdAt;

    public KeyStatementResponse(
            UUID statementId,
            String content,
            String description,
            LocalDateTime createdAt
    ) {
        this.statementId = statementId;
        this.content = content;
        this.description = description;
        this.createdAt = createdAt;
    }
}