package com.aivle.big_project.chat_log.service;

import com.aivle.big_project.ai.AiClient;
import com.aivle.big_project.ai.dto.chat.ChatCreateRequest;
import com.aivle.big_project.ai.dto.chat.ChatCreateResponse;
import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.chat_log.entity.ChatLogEntity;
import com.aivle.big_project.chat_log.repository.ChatLogRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatAsyncWorker {
    private final AiClient aiClient;
    private final ChatLogRepository chatLogRepository;
    private final ObjectMapper objectMapper;

    @Async
    @Transactional
    public void requestAiAndSaveAsync(UUID caseId, UUID aiChatId, String userMessage) {
        ChatCreateRequest request = ChatCreateRequest.builder()
                .role("user")
                .message(userMessage)
                .build();

        ChatCreateResponse response;
        try {
            response = aiClient.respondChat(request);
        } catch (Exception e) {
            // “실패 메시지 업데이트” 처리
            updateFailure(aiChatId);
            return;
        }

        String usedContextJson = null;
        if (response != null && response.getUsedContext() != null) {
            try {
                usedContextJson = objectMapper.writeValueAsString(response.getUsedContext());
            } catch (Exception ignored) {
            }
        }

        ChatLogEntity aiRow = chatLogRepository.findById(aiChatId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_LOG_NOT_FOUND));

        if (response != null) {
            aiRow.updateChatContent(response.getMessage(), usedContextJson);
        } else {
            throw new CustomException(ErrorCode.EMPTY_CHAT_LOG);
        }
        // save 호출 없이도 영속 상태면 flush로 반영되지만, 명시 save는 유지해도 무방합니다.
        chatLogRepository.save(aiRow);
    }

    private void updateFailure(UUID aiChatId) {
        ChatLogEntity aiRow = chatLogRepository.findById(aiChatId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_LOG_NOT_FOUND));
        aiRow.updateChatContent("AI 응답 생성에 실패했습니다. 잠시 후 다시 시도해 주세요.", null);
        chatLogRepository.save(aiRow);
    }
}
