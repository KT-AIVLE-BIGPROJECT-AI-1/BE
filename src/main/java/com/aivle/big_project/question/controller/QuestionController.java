package com.aivle.big_project.question.controller;

import com.aivle.big_project.question.dto.QuestionListResponse;
import com.aivle.big_project.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cases")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/{caseId}/contradiction/{contradictionId}/questions")
    public ResponseEntity<QuestionListResponse> getQuestions(
            @PathVariable UUID caseId,
            @PathVariable UUID contradictionId
    ) {
        QuestionListResponse response =
                questionService.getQuestions(caseId, contradictionId);

        return ResponseEntity.ok(response);
    }
}