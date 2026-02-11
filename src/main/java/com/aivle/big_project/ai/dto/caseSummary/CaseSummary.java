package com.aivle.big_project.ai.dto.caseSummary;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CaseSummary {
    private String action;
    private String object;
    private String interaction;
    private String place;
    private String time;
    private String result;
}
