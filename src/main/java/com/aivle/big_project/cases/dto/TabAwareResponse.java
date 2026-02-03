package com.aivle.big_project.cases.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor @AllArgsConstructor
public class TabAwareResponse<T> {

    private T data;

    @JsonProperty("tab_flags")
    private TabFlags tabFlags;
}
