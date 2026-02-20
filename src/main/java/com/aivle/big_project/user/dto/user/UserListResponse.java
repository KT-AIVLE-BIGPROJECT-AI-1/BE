package com.aivle.big_project.user.dto.user;

import com.aivle.big_project.user.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonAppend;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserListResponse {
    private List<UserItem> users;

    @Getter @Builder
    public static class UserItem {
        private UUID userId;
        private String email;
        private String name;

        private Role role;

        private String department;
        private String jurisdictionStation;
        private String rankName;

        private String phoneNumber;
        private String profileImg;
    }
}
