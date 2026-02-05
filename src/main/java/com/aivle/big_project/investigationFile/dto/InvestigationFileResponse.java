package com.aivle.big_project.investigationFile.dto;

import com.aivle.big_project.investigationFile.entity.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class InvestigationFileResponse {

    private UUID file_id;
    private UUID case_id;
    private FileType type;
    private String origin_name;
    private String filepath;
    private Long fileSize;
    private LocalDateTime created_at;
}