package com.aivle.big_project.cases.client;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiSummaryClient {


//   * @return summaryJson: 구조화 요약(JSON 문자열). 실패/미구현 시 null 반환.
    public SummaryResult summarize(SummaryCommand command) {
        log.warn("AiSummaryClient is not configured yet. Returning null summary. title={}", command.title());
        return SummaryResult.builder().summaryJson(null).build();
    }

    @Builder
    public record SummaryCommand(
            String title,
            String description,
            String type,
            String location
    ) {}

    @Builder
    public record SummaryResult(
            String summaryJson // JSON 문자열(=structured summary)
    ) {}
}
