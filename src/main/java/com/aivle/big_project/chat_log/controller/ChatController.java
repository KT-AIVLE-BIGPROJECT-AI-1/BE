package com.aivle.big_project.chat_log.controller;

import com.aivle.big_project.chat_log.dto.ChatItemResponse;
import com.aivle.big_project.chat_log.dto.ChatListResponse;
import com.aivle.big_project.chat_log.dto.ChatSendRequest;
import com.aivle.big_project.chat_log.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cases/{caseId}/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ChatListResponse> getChats(
            @PathVariable UUID caseId
    ) {
        ChatListResponse response = chatService.getChats(caseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ChatItemResponse> sendChat(
            @PathVariable UUID caseId,
            @RequestBody @Valid ChatSendRequest request
    ) {
        return ResponseEntity.ok(
                chatService.sendChat(caseId, request.getMessage())
        );
    }
}