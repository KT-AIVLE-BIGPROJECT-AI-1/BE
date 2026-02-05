package com.aivle.big_project.investigationFile.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class InvestigationFileListItem {

    private UUID fileId;
    private String type;
    private String originName;
    private LocalDateTime createdAt;

    public InvestigationFileListItem(
            UUID fileId,
            String type,
            String originName,
            LocalDateTime createdAt
    ) {
        this.fileId = fileId;
        this.type = type;
        this.originName = originName;
        this.createdAt = createdAt;
    }

}