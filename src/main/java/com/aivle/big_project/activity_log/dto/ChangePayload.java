package com.aivle.big_project.activity_log.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChangePayload {

    private String entityId;                // case_id, report_id 등

    private Map<String, Object> before;     // 변경 전 스냅샷
    private Map<String, Object> after;      // 변경 후 스냅샷

    private List<String> changedFields;     // 변경된 속성명 리스트
}
