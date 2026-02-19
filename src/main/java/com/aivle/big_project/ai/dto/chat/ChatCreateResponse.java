package com.aivle.big_project.ai.dto.chat;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.UUID;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChatCreateResponse {
    private String message;
    private ChatContext usedContext;

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ChatContext {
        private List<UUID> analysisIds;
        private String type;
    }
}
