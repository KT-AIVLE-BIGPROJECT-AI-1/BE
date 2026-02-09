package com.aivle.big_project.audit_log.dto;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuditTargetUserResponse {
    private UUID userId;

    private String name;
    private String email;
    private String phoneNumber;

    private String role;
    private String profileImg;

    private String jurisdictionStation;
    private String department;
    private String rankName;
}
