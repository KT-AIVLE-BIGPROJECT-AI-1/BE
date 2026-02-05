package com.aivle.big_project.criteria.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaListResponse {

    private List<CriteriaItem> criteria;
}
