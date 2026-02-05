package com.aivle.big_project.contradiction.service;

import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.contradiction.dto.ContradictionListResponse;
import com.aivle.big_project.contradiction.dto.ContradictionResponse;
import com.aivle.big_project.contradiction.entity.ContradictionEntity;
import com.aivle.big_project.contradiction.repository.ContradictionRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ContradictionService {

    private final ContradictionRepository contradictionRepository;

    public ContradictionListResponse getContradictions(UUID caseId) {

        List<ContradictionEntity> contradictions = contradictionRepository.findAllByCaseId(caseId);

        List<ContradictionResponse> responses =
                contradictions.stream()
                        .map(ContradictionResponse::from)
                        .toList();

        return ContradictionListResponse.of(caseId, responses);
    }
}