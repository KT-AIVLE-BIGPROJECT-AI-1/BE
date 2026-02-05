package com.aivle.big_project.activity_log.dto;

import com.aivle.big_project.activity_log.entity.ActivityLog;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ActivityLogListResponse {
    private List<ActivityLogResponse> logs;

    public static ActivityLogListResponse from(List<ActivityLog> entities) {
        return ActivityLogListResponse.builder()
                .logs(entities.stream().map(ActivityLogListResponse::toItem).toList())
                .build();
    }

    private static ActivityLogResponse toItem(ActivityLog log) {
        return ActivityLogResponse.builder()
                .logId(log.getLogId())
                .caseId(log.getACase().getCaseId())
                .field(log.getField().name())              // enum이면 name()
                .activityType(log.getActivityType().name())
                .payload(log.getPayload())
                .createdAt(log.getCreatedAt())
                .status(log.getStatus().name())
                .reportId(log.getReport() == null ? null : log.getReport().getReportId())
                .build();
    }
}
