package com.aivle.big_project.question.dto;

import com.aivle.big_project.question.entity.QuestionEntity;
import lombok.*;

import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionListResponse {

    private UUID contradictionId;
    private List<QuestionItemResponse> questions;

    public static QuestionListResponse of(
            UUID contradictionId,
            List<QuestionEntity> entities
    ) {
        List<QuestionItemResponse> items = entities.stream()
                .map(QuestionItemResponse::from)
                .toList();

        return new QuestionListResponse(contradictionId, items);
    }
}