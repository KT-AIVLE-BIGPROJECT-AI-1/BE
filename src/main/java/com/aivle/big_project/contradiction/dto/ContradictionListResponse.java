package com.aivle.big_project.contradiction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ContradictionListResponse {

    private UUID caseId;
    private List<ContradictionResponse> contradictions;

    public static ContradictionListResponse of(
            UUID caseId,
            List<ContradictionResponse> contradictions
    ) {
        return new ContradictionListResponse(caseId, contradictions);
    }
}