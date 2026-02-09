package com.aivle.big_project.question.entity;

import com.aivle.big_project.contradiction.entity.ContradictionEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "question")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionEntity {

    @Id
    @Column(name = "question_id", columnDefinition = "UUID")
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contradiction_id", nullable = false)
    private ContradictionEntity contradiction;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "rationale", columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}