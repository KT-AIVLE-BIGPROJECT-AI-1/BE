package com.aivle.big_project.chat_log.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final CaseRepository caseRepository;
    private final AnalysisRepository analysisRepository;
    private final ChatLogRepository chatLogRepository;

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

        // 채팅 저장
        ChatLogEntity userChat = chatLogRepository.save(
                ChatLogEntity.builder()
                        .chatId(UUID.randomUUID())
                        .caseEntity(caseEntity)
                        .role("USER")
                        .message(message)
                        .usedContext(null)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return ChatItemResponse.from(userChat, caseId);
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