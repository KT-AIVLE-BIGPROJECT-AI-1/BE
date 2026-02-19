package com.aivle.big_project.investigationFile.dto;

import com.aivle.big_project.investigationFile.entity.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InvestigationFileResponse {

    private UUID fileId;
    private UUID caseId;
    private FileType type;
    private String originName;
    private String filepath;
    private Long fileSize;
    private LocalDateTime createdAt;
}