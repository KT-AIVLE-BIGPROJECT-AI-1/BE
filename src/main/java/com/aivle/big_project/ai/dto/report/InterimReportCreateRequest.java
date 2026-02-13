package com.aivle.big_project.ai.dto.report;

import com.aivle.big_project.ai.dto.caseSummary.CaseDetail;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InterimReportCreateRequest {
    private CaseDetail caseDetail;
    private List<ActivityLog> selectedActivityLogs;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ActivityLog {
        private UUID logId;
        private String field;
        private String activityType;
        private String payload;
        private String createdAt;
    }
}


