package com.aivle.big_project.ai.dto.analysis;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Analysis {
    private String fileType;
    private String status;
    private AiCallbackRequest resultData;
}

