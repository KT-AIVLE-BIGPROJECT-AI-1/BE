package com.aivle.big_project.contradiction.service;

import com.aivle.big_project.activity_log.entity.ActivityLogField;
import com.aivle.big_project.activity_log.entity.ActivityType;
import com.aivle.big_project.activity_log.service.ActivityLogService;
import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.contradiction.dto.ContradictionListResponse;
import com.aivle.big_project.contradiction.dto.ContradictionResponse;
import com.aivle.big_project.contradiction.dto.UpdateContradictionStatusRequest;
import com.aivle.big_project.contradiction.dto.UpdateContradictionStatusResponse;
import com.aivle.big_project.contradiction.entity.ContradictionEntity;
import com.aivle.big_project.contradiction.repository.ContradictionRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContradictionService {

    private final ContradictionRepository contradictionRepository;
    private final ActivityLogService activityLogService;

    public ContradictionListResponse getContradictions(UUID caseId) {

        List<ContradictionEntity> contradictions = contradictionRepository.findAllByCaseId(caseId);

        List<ContradictionResponse> responses =
                contradictions.stream()
                        .map(ContradictionResponse::from)
                        .toList();

        return ContradictionListResponse.of(caseId, responses);
    }

    @Transactional
    public UpdateContradictionStatusResponse updateStatus(
            UUID caseId,
            UUID contradictionId,
            UpdateContradictionStatusRequest request
    ) {
        ContradictionEntity contradiction =
                contradictionRepository
                        .findByContradictionIdAndCaseEntity_CaseId(contradictionId, caseId)
                        .orElseThrow(() ->
                                new CustomException(ErrorCode.CONTRADICTION_NOT_FOUND)
                        );

        var before = contradiction.getStatus();
        try {
            contradiction.changeStatus(request.getStatus());
        } catch (Exception e) {
            throw new CustomException(
                    ErrorCode.INVALID_CONTRADICTION_STATUS_TRANSITION
            );
        }

        activityLogService.log(
                contradiction.getCaseEntity().getCaseId(),
                ActivityLogField.CONTRADICTION,
                ActivityType.UPDATE,
                p -> {
                    p.put("contradictionId", contradiction.getContradictionId());
                    p.put("before", before.name());
                    p.put("after", contradiction.getStatus().name());
                }
        );

        return new UpdateContradictionStatusResponse(
                contradiction.getContradictionId(),
                contradiction.getStatus(),
                LocalDateTime.now()
        );
    }
}