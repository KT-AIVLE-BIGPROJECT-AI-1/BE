package com.aivle.big_project.question.service;

import com.aivle.big_project.contradiction.repository.ContradictionRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import com.aivle.big_project.question.dto.QuestionListResponse;
import com.aivle.big_project.question.entity.QuestionEntity;
import com.aivle.big_project.question.repository.QuestionRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ContradictionRepository contradictionRepository;

    public QuestionListResponse getQuestions(
            UUID caseId,
            UUID contradictionId
    ) {
        // 모순 존재 + 사건 소속
        contradictionRepository.findByContradictionIdAndCaseEntity_CaseId(contradictionId, caseId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONTRADICTION_NOT_FOUND));

        List<QuestionEntity> questions =
                questionRepository.findByContradictionId(contradictionId);

        return QuestionListResponse.of(contradictionId, questions);
    }
}