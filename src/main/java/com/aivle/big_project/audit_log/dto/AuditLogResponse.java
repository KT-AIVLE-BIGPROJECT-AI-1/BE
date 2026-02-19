package com.aivle.big_project.audit_log.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuditLogResponse {
    private UUID auditId;

    private String action;       // 예: CREATE, LOGIN, AI_REQUEST...
    private String targetType;   // 예: CASE, ANALYSIS, REPORT...
    private UUID targetId;       // 구체 target 객체 UUID (없으면 null)

    private Map<String, Object> metadata; // jsonb

    // 관할서 추출을 위해 userId 추가
    private UUID userId;

    private LocalDateTime createdAt;
}
