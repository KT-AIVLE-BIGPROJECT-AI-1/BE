package com.aivle.big_project.contradiction.dto;

import com.aivle.big_project.contradiction.entity.ContradictionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateContradictionStatusResponse {

    private UUID contradictionId;
    private ContradictionStatus status;
    private LocalDateTime updatedAt;
}