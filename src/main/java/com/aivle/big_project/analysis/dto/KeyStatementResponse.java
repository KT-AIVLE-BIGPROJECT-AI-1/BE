package com.aivle.big_project.analysis.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class KeyStatementResponse {

    private final UUID statementId;
    private final String transcript;
    private final String description;
    private final LocalDateTime createdAt;

    public KeyStatementResponse(
            UUID statementId,
            String transcript,
            String description,
            LocalDateTime createdAt
    ) {
        this.statementId = statementId;
        this.transcript = transcript;
        this.description = description;
        this.createdAt = createdAt;
    }
}