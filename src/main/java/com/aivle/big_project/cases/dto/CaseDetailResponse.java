package com.aivle.big_project.cases.dto;

import com.aivle.big_project.cases.entity.CaseStatus;
import com.aivle.big_project.cases.entity.CaseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter @Builder
public class CaseDetailResponse {

    @JsonProperty("case_id")
    private UUID caseId;

    // 담당자 정보
    @JsonProperty("assignee_user_id")
    private UUID assigneeUserId;
    @JsonProperty("assignee_user_name")
    private String assigneeUserName;
    @JsonProperty("assignee_user_department")
    private String assigneeUserDepartment;
    @JsonProperty("assignee_user_rank")
    private String assigneeUserRank;

    private String title;
    private String description; // 사건 개요
    private String summary; // AI 분석 내용 - JSON 그대로

    private String type;
    private String status;

    @JsonProperty("occurred_at")
    private LocalDateTime occurredAt;
    private String location;

    @JsonProperty("can_edit")
    private boolean canEdit;

    @JsonProperty("created_at")
    private Instant createdAt;
    @JsonProperty("updated_at")
    private Instant updatedAt;
}
