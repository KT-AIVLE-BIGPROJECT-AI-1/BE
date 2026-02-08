package com.aivle.big_project.chat_log.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatListResponse {

    private UUID caseId;
    private List<ChatItemResponse> chats;
}