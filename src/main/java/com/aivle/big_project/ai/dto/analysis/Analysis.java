package com.aivle.big_project.ai.dto.analysis;

import lombok.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Analysis {
    private UUID analysisId;
    private String fileType;
    private String status;
    private JsonNode resultData;
}

