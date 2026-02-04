package com.aivle.big_project.report.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReportDraftCreateRequest {
    private String type; // 보고서 유형 (INTERIM, FINAL, TRANSFER, ...)
    private String title;
    private String content; // 보고서 내용 : json 문자열

    private List<UUID> selectedLogIds; // INTERIM(중간보고)에서만 사용
    private UUID finalId; // FINAL(결과보고)에서만 사용
}
