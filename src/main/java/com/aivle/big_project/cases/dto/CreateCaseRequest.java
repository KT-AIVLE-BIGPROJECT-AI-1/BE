package com.aivle.big_project.cases.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class CreateCaseRequest {
    @NotBlank
    private String title;
    private String description;
    private String summary; // AI 구조화 요약 내용
    @NotBlank
    private String type;
    @JsonProperty("occured_at")
    private LocalDateTime occuredAt; // 발생 일시
    private String location; // 발생 장소 (주소)
}
