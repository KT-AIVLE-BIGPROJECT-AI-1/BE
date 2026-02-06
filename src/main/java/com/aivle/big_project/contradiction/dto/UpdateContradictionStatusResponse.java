package com.aivle.big_project.contradiction.dto;

import com.aivle.big_project.contradiction.entity.ContradictionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateContradictionStatusResponse {

    private UUID contradictionId;
    private ContradictionStatus status;
    private LocalDateTime updatedAt;
}