package com.aivle.big_project.user.dto.user;

import com.aivle.big_project.user.entity.Role;
import lombok.Builder;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.UUID;

@Getter @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MyDetailResponse {
    private UUID userId;

    private String name;
    private String email;
    private String phoneNumber;

    private Role role;

    private String profileImg;

    private String jurisdictionStation;
    private String department;
    private String rankName;

    private Instant createdAt;
    private Instant updatedAt;
}
