package com.aivle.big_project.ai.dto.analysis.payload;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContradictionPayload {
    private String statementTempId;
    private String counterStatementTempId;
    private String description;
    private String type;
}
