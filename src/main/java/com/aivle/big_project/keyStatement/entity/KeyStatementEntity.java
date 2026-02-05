package com.aivle.big_project.keyStatement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "key_statement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class KeyStatementEntity {

    @Id
    @GeneratedValue
    @Column(name = "statement_id")
    private UUID statementId;

    @Column(name = "analysis_id", nullable = false)
    private UUID analysisId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}