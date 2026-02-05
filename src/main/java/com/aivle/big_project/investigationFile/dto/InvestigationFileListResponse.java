package com.aivle.big_project.investigationFile.dto;

import java.util.List;

public class InvestigationFileListResponse {

    private List<InvestigationFileListItem> data;

    public InvestigationFileListResponse(List<InvestigationFileListItem> data) {
        this.data = data;
    }

    public List<InvestigationFileListItem> getData() {
        return data;
    }
}