package com.aivle.big_project.chat_log.service;

import com.aivle.big_project.ai.AiClient;
import com.aivle.big_project.ai.dto.chat.ChatCreateRequest;
import com.aivle.big_project.ai.dto.chat.ChatCreateResponse;
import com.aivle.big_project.analysis.entity.AnalysisStatus;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.repository.CaseRepository;
import com.aivle.big_project.chat_log.dto.ChatItemResponse;
import com.aivle.big_project.chat_log.dto.ChatListResponse;
import com.aivle.big_project.chat_log.repository.ChatLogRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.chat_log.entity.ChatLogEntity;
import com.aivle.big_project.global.exception.ErrorCode;
import com.aivle.big_project.investigationFile.entity.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@EnableAsync
public class ChatService {

    private final CaseRepository caseRepository;
    private final AnalysisRepository analysisRepository;
    private final ChatLogRepository chatLogRepository;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final ChatAsyncWorker worker;

    public ChatListResponse getChats(UUID caseId) {

        validateVideoAnalysisCompleted(caseId);

        // 채팅 조회 (최신 → 과거)
        List<ChatLogEntity> logs =
                chatLogRepository.findByCaseIdOrderByCreatedAtDesc(caseId);

        List<ChatItemResponse> chats = logs.stream()
                .map(log -> ChatItemResponse.from(log, caseId))
                .toList();

        return new ChatListResponse(caseId, chats);
    }

    @Transactional
    public ChatItemResponse sendChat(UUID caseId, String message) {

        CaseEntity caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new CustomException(ErrorCode.CASE_NOT_FOUND));

        validateVideoAnalysisCompleted(caseId);

        // 1) 사용자 채팅 저장
        ChatLogEntity userChat = chatLogRepository.save(
                ChatLogEntity.builder()
                        .caseEntity(caseEntity)
                        .role("USER")
                        .message(message)
                        .usedContext(null)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // 2) AI 응답 placeholder 저장 (프론트는 polling으로 이후 갱신)
        ChatLogEntity pendingAiChat = chatLogRepository.save(
                ChatLogEntity.builder()
                        .caseEntity(caseEntity)
                        .role("AI")
                        .message("")
                        .usedContext(null)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // 3) 비동기 AI 호출 시작 (즉시 반환)
        worker.requestAiAndSaveAsync(caseId, pendingAiChat.getChatId(), message);

        // 4) 프론트에는 placeholder(AI) row를 반환 (polling으로 message가 채워짐)
        return ChatItemResponse.from(pendingAiChat, caseId);
    }

    // 공통 로직 (video 분석 1개 이상 존재하는지)
    private void validateVideoAnalysisCompleted(UUID caseId) {
        boolean hasCompletedVideoAnalysis =
                analysisRepository.existsCompletedAnalysisByCaseIdAndType(
                        caseId,
                        FileType.VIDEO,
                        AnalysisStatus.COMPLETED
                );

        if (!hasCompletedVideoAnalysis) {
            throw new CustomException(ErrorCode.ANALYSIS_NOT_COMPLETED);
        }
    }
}