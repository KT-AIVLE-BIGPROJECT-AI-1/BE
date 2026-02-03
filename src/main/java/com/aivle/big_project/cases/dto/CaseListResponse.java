package com.aivle.big_project.cases.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseListResponse {
    private List<CaseSummaryResponse> cases;
    private PageInfo info;
}
