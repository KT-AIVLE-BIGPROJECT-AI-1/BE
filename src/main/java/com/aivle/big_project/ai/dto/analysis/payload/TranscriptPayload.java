package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class TranscriptPayload {
    private List<TranscriptLine> content;

    @Getter @Setter
    public static class TranscriptLine {
        private String speaker;
        private String content;
    }
}
