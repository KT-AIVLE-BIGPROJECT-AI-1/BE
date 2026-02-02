package com.aivle.big_project.user.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;
}
