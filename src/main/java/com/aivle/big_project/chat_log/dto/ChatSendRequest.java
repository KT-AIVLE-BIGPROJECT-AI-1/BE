package com.aivle.big_project.chat_log.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatSendRequest {

    @NotBlank
    private String message;
}