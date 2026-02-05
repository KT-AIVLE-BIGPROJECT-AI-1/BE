package com.aivle.big_project.investigationFile.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class InvestigationFileListResponse {

    private List<InvestigationFileListItem> data;

    public InvestigationFileListResponse(List<InvestigationFileListItem> data) {
        this.data = data;
    }

}