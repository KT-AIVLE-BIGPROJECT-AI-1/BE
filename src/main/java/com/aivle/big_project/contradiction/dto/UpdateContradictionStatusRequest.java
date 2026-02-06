package com.aivle.big_project.contradiction.dto;

import com.aivle.big_project.contradiction.entity.ContradictionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateContradictionStatusRequest {

    @NotNull
    private ContradictionStatus status;
}