package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TranscriptPayload {
    private List<TranscriptLine> content;

    @Getter @Setter
    public static class TranscriptLine {
        private String tempId; // transcriptId
        private String speaker;
        private String content;
    }
}
