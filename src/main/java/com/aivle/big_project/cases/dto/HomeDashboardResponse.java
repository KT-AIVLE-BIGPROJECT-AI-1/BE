package com.aivle.big_project.cases.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class HomeDashboardResponse {

    @JsonProperty("dashboard")
    private Dashboard dashboard;

    @JsonProperty("cases")
    private List<CaseSummaryResponse> cases;

    @JsonProperty("info")
    private PageInfo info;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dashboard {
        private long open;
        private long ongoing;
        private long done;

        @JsonProperty("type_counts")
        private Map<String,Long> typeCounts;
    }
}
