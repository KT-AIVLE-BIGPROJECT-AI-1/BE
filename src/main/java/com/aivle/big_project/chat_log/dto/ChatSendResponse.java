package com.aivle.big_project.chat_log.dto;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSendResponse {

    private UUID caseId;
    private ChatItemResponse userChat;
}