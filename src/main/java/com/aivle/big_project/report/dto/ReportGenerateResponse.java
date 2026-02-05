package com.aivle.big_project.report.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportGenerateResponse {
    private UUID caseId;
    private String type; // 보고서 유형 (수사보고/결과보고/결정서)
    private String title;
    private String content; // 초안 내용  JSON
    private List<UUID> selectedLogIds; // (중간) 수사보고일 때만
}
