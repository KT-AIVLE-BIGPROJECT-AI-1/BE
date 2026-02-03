package com.aivle.big_project.cases.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter @NoArgsConstructor
@AllArgsConstructor
public class UpdateCaseAssigneeRequest {
    @NotNull
    @JsonProperty("assignee_user_id")
    private UUID assigneeUserId;
}
