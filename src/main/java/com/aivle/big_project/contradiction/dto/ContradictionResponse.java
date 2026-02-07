package com.aivle.big_project.contradiction.dto;

import com.aivle.big_project.contradiction.entity.ContradictionEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContradictionResponse {

    private UUID contradictionId;
    private String type;
    private String status;
    private String description;

    private ContradictionKeyStatementResponse statement1;
    private ContradictionKeyStatementResponse statement2;

    public static ContradictionResponse from(ContradictionEntity entity) {
        return new ContradictionResponse(
                entity.getContradictionId(),
                entity.getType(),
                entity.getStatus().name(),
                entity.getDescription(),
                ContradictionKeyStatementResponse.from(entity.getStatement1()),
                ContradictionKeyStatementResponse.from(entity.getStatement2())
        );
    }
}