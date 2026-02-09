package com.aivle.big_project.question.dto;

import com.aivle.big_project.question.entity.QuestionEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionItemResponse {

    private UUID questionId;
    private String content;
    private String rationale;
    private LocalDateTime createdAt;

    public static QuestionItemResponse from(QuestionEntity entity) {
        return new QuestionItemResponse(
                entity.getQuestionId(),
                entity.getContent(),
                entity.getRationale(),
                entity.getCreatedAt()
        );
    }
}