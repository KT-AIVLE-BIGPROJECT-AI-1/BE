package com.aivle.big_project.criteria.dto;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class CriteriaDetailResponse {
    private UUID criteriaId;
    private String criterionText;
    private IssueInfo issue;
    private PrecedentInfo precedent;
    private EvaluationInfo evaluation;

    @Getter
    @AllArgsConstructor
    public static class IssueInfo {
        private String issueName;
        private String relatedLaw;
    }

    @Getter
    @AllArgsConstructor
    public static class PrecedentInfo {
        private String caseNo;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class EvaluationInfo {
        private String status;
        private String reason;
        private List<String> evidenceSupplementPoints; // null 허용
    }
}
