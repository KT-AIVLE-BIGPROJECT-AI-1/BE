package com.aivle.big_project.user.dto.user;

import com.aivle.big_project.user.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateUserRoleRequest {
    @NotNull
    private Role role;
}
