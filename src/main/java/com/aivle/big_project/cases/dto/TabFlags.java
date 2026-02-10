package com.aivle.big_project.cases.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TabFlags {
    /*
    * 사건 상세 탭 활성화 여부를 알려주는 객체
    * 사건 개요(필수) / 법리기준 체크리스트(필수) / 자료 업로드 및 분석(필수) /
    * 진술 모순 분석 / 영상 분석 챗봇 / 보고서 목록 */
    @JsonProperty("has_contradictions")
    private boolean hasContradictions; // 진술모순분석

    @JsonProperty("has_video_files")
    private boolean hasVideoFiles; // 영상분석

    @JsonProperty("has_finalAnalysis")
    private boolean hasFinalAnalysis; // 보고서
}
