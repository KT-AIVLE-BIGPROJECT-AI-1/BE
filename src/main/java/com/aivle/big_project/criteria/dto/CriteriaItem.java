package com.aivle.big_project.criteria.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaItem {

    private UUID criteriaId;
    private String criterionText;
    private String issueName;
    private String relatedLaw;
    private String precedentCaseNo;
}
