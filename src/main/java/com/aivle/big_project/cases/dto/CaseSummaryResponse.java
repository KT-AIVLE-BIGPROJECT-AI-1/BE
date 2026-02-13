package com.aivle.big_project.cases.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class CaseSummaryResponse {

    @JsonProperty("case_id")
    private UUID caseId;

    private String title;
    private String type;
    private String status;

    @JsonProperty("occurred_at")
    private LocalDateTime occurredAt;

    private String location;

    @JsonProperty("assignee_user_id")
    private UUID assigneeUserId; // 담당자 User Id

    @JsonProperty("assignee_name")
    private String assigneeName; // 담당자 User name

    @JsonProperty("updated_at")
    private Instant updatedAt;
}
