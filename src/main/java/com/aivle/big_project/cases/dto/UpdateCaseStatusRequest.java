package com.aivle.big_project.cases.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
@AllArgsConstructor
public class UpdateCaseStatusRequest {

    @NotBlank
    private String status; // OPEN | ONGOING | DONE 중으로 받기
}
