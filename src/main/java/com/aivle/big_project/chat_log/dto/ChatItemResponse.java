package com.aivle.big_project.chat_log.dto;

import com.aivle.big_project.chat_log.entity.ChatLogEntity;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatItemResponse {

    private UUID chatId;
    private UUID caseId;
    private String role;
    private String message;
    private String usedContext;
    private LocalDateTime createdAt;

    public static ChatItemResponse from(ChatLogEntity entity, UUID caseId) {
        return new ChatItemResponse(
                entity.getChatId(),
                caseId,
                entity.getRole(),
                entity.getMessage(),
                entity.getUsedContext(),
                entity.getCreatedAt()
        );
    }
}