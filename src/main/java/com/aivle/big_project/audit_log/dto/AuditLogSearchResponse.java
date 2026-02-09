package com.aivle.big_project.audit_log.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuditLogSearchResponse {

    private AuditTargetUserResponse user;
    private List<AuditLogResponse> logs;
}
