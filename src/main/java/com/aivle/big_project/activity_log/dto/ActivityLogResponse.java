package com.aivle.big_project.activity_log.dto;

import com.aivle.big_project.activity_log.entity.ActivityLogField;
import com.aivle.big_project.activity_log.entity.ActivityLogStatus;
import com.aivle.big_project.activity_log.entity.ActivityType;
import com.aivle.big_project.cases.entity.Case;
import com.aivle.big_project.report.entity.Report;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ActivityLogResponse {

    private UUID logId;
    private UUID caseId;

    private String field;  // 테이블명
    private String activityType;  // Create, Update, Delete

    private String payload;  // 로그 세부내용
    private Instant createdAt;

    private UUID reportId; // 중간보고 귀속 여부 판단에 사용
    private String status; // 중간보고 귀속 여부

}