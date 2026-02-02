package com.aivle.big_project.user.dto.auth;

import com.aivle.big_project.user.entity.Role;
import lombok.Builder;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Role role;
    private long expiresIn;
}
