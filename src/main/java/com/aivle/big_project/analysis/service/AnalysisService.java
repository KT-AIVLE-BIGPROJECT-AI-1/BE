package com.aivle.big_project.analysis.service;

import com.aivle.big_project.ai.AiClient;
import com.aivle.big_project.ai.AiClientException;
import com.aivle.big_project.ai.dto.analysis.*;
import com.aivle.big_project.ai.dto.analysis.payload.*;
import com.aivle.big_project.ai.dto.caseSummary.CaseDetail;
import com.aivle.big_project.ai.dto.caseSummary.CaseSummary;
import com.aivle.big_project.ai.dto.legalCriteria.LegalCriteria;
import com.aivle.big_project.ai.dto.legalCriteria.LegalIssue;
import com.aivle.big_project.analysis.dto.*;
import com.aivle.big_project.analysis.entity.AnalysisEntity;
import com.aivle.big_project.analysis.entity.AnalysisStatus;
import com.aivle.big_project.analysis.repository.AnalysisRepository;
import com.aivle.big_project.cases.entity.CaseEntity;
import com.aivle.big_project.cases.repository.CaseRepository;
import com.aivle.big_project.contradiction.entity.ContradictionEntity;
import com.aivle.big_project.contradiction.entity.ContradictionStatus;
import com.aivle.big_project.criteria.entity.CriteriaEntity;
import com.aivle.big_project.criteria.entity.LegalIssueEntity;
import com.aivle.big_project.criteria.repository.CriteriaRepository;
import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import com.aivle.big_project.investigationFile.repository.InvestigationFileRepository;
import com.aivle.big_project.keyStatement.entity.KeyStatementEntity;
import com.aivle.big_project.keyStatement.repository.KeyStatementRepository;
import com.aivle.big_project.question.entity.QuestionEntity;
import com.aivle.big_project.storage.ObjectStorageService;
import com.aivle.big_project.transcript.entity.TranscriptEntity;
import com.aivle.big_project.transcript.repository.TranscriptRepository;
import com.aivle.big_project.videoEvent.entity.VideoEventEntity;
import com.aivle.big_project.videoEvent.repository.VideoEventRepository;
import com.aivle.big_project.global.exception.CustomException;
import com.aivle.big_project.global.exception.ErrorCode;
import com.aivle.big_project.investigationFile.entity.FileType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.*;

@Service @Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final KeyStatementRepository keyStatementRepository;
    private final VideoEventRepository videoEventRepository;
    private final TranscriptRepository transcriptRepository;

    private final CaseRepository caseRepository;
    private final InvestigationFileRepository investigationFileRepository;
    private final CriteriaRepository criteriaRepository;
    private final ObjectStorageService objectStorageService;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;

    // 분석 상태 조회
    public AnalysisStatusResponse getStatus(UUID analysisId) {
        AnalysisEntity analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.ANALYSIS_NOT_FOUND)
                );

        return new AnalysisStatusResponse(
                analysis.getAnalysisId(),
                analysis.getStatus()
        );
    }

    // 분석 상세 조회
    public AnalysisDetailResponse getAnalysisDetail(UUID analysisId) {
        AnalysisEntity analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_NOT_FOUND));

        if (analysis.getStatus() != AnalysisStatus.COMPLETED) {
            throw new CustomException(ErrorCode.ANALYSIS_NOT_COMPLETED);
        }

        FileType fileType = analysis.getFile().getFileType();

        return switch (fileType) {
            case DOCUMENT, IMAGE -> buildDocumentImage(analysis);
            case AUDIO -> buildAudio(analysis);
            case VIDEO -> buildVideo(analysis);
        };
    }

    private AnalysisDetailResponse buildDocumentImage(AnalysisEntity analysis) {
        return new AnalysisDetailResponse(
                analysis.getAnalysisId(),
                analysis.getFile().getFileType(),
                new DocumentImageAnalysisData(analysis.getResultData())
        );
    }

    private AnalysisDetailResponse buildAudio(AnalysisEntity analysis) {

        List<TranscriptEntity> transcripts =
                transcriptRepository.findAllByAnalysis_AnalysisIdOrderByCreatedAtAsc(analysis.getAnalysisId());

        if (transcripts.isEmpty()) {
            throw new CustomException(ErrorCode.TRANSCRIPT_NOT_FOUND);
        }

        List<KeyStatementResponse> statements =
                transcripts.stream()
                        .flatMap(t ->
                                keyStatementRepository
                                        .findByTranscript_TranscriptId(t.getTranscriptId())
                                        .stream()
                        )
                        .map(s -> new KeyStatementResponse(
                                s.getStatementId(),
                                s.getStatementText(),
                                s.getDescription(),
                                s.getCreatedAt()
                        ))
                        .toList();

        return new AnalysisDetailResponse(
                analysis.getAnalysisId(),
                analysis.getFile().getFileType(),
                new AudioAnalysisData(
                        analysis.getResultData(),
                        statements
                )
        );
    }

    private AnalysisDetailResponse buildVideo(AnalysisEntity analysis) {
        List<VideoEventResponse> events =
                videoEventRepository.findByAnalysisId(analysis.getAnalysisId())
                        .stream()
                        .map(e -> new VideoEventResponse(
                                e.getEventId(),
                                e.getStartTime(),
                                e.getEndTime(),
                                e.getEventType(),
                                e.getEventDescription(),
                                e.getCreatedAt()
                        ))
                        .toList();

        return new AnalysisDetailResponse(
                analysis.getAnalysisId(),
                analysis.getFile().getFileType(),
                new VideoAnalysisData(
                        analysis.getResultData(),
                        events
                )
        );
    }

    /**
     * 파일 업로드 완료 후, 이번 업로드 요청에서 업로드된 파일들만
     * 1) 파일과 1:1인 analysis 튜플을 생성(없으면 생성)
     * 2) presignedUrl/contentType을 생성
     * 3) (caseDetail, investigationFiles, legalCriteria) DTO를 완성해 AI 서버로 요청
     */
    @Transactional
    public void requestInvestigationFilesAnalysis(UUID caseId, List<UUID> uploadedFileIds) {
        try {
            CaseEntity aCase = caseRepository.findById(caseId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CASE_NOT_FOUND));

            // 이번 업로드 요청에서 저장된 파일들만 대상으로 분석 요청
            if (uploadedFileIds == null || uploadedFileIds.isEmpty()) {
                log.info("분석할 파일이 업로드 되지 않았습니다. caseId={}", caseId);
                return;
            }
            List<InvestigationFileEntity> files = investigationFileRepository.findAllById(uploadedFileIds)
                    .stream()
                    .filter(f -> caseId.equals(f.getCaseId()))
                    .toList();

            if (files.isEmpty()) {
                log.warn("본 사건에서 현재 업로드한 파일이 존재하지 않습니다. caseId={}, uploadedFileIds={}", caseId, uploadedFileIds);
                return;
            }

            // case_detail.summary 파싱 (DB에 JSON 문자열로 저장된 경우 대응)
            CaseSummary parsedSummary = null;
            if (aCase.getSummary() != null && !aCase.getSummary().isBlank()) {
                try {
                    parsedSummary = objectMapper.readValue(aCase.getSummary(), CaseSummary.class);
                } catch (Exception e) {
                    log.warn("사건 entity 에서 summary 정보 파싱에 실패했습니다. caseId={}, reason={}", caseId, e.getMessage(), e);
                }
            }

            CaseDetail caseDetail = CaseDetail.builder()
                    .caseId(aCase.getCaseId())
                    .title(aCase.getTitle())
                    .description(aCase.getDescription())
                    .summary(parsedSummary)
                    .type(aCase.getType() == null ? null : aCase.getType().name())
                    .status(aCase.getStatus() == null ? null : aCase.getStatus().name())
                    .occurredAt(aCase.getOccuredAt())
                    .location(aCase.getLocation())
                    .build();

            // investigation_files
            List<InvestigationFile> invFiles = new ArrayList<>();
            for (InvestigationFileEntity f : files) {
                // 파일과 1:1 관계인 Analysis를 조회하고, 없으면 생성(PENDING)
                AnalysisEntity analysis = analysisRepository.findByFile(f)
                        .orElseGet(() -> {
                            AnalysisEntity created = AnalysisEntity.builder()
                                    .file(f)
                                    .status(AnalysisStatus.PENDING)
                                    .build();
                            return analysisRepository.save(created);
                        });

                String presignedUrl = null;
                try {
                    presignedUrl = objectStorageService.presignGetUrl(f.getFilePath());
                } catch (Exception e) {
                    log.warn("파일 접근을 위한 Presigned URL 생성에 실패했습니다. caseId={}, fileId={}, key={}, reason={}",
                            caseId, f.getFileId(), f.getFilePath(), e.getMessage(), e);
                }

                String contentType = null;
                try {
                    contentType = objectStorageService.getContentType(f.getFilePath());
                } catch (Exception e) {
                    log.warn("S3로부터 파일 Content Type을 가져오는 데 실패했습니다. caseId={}, fileId={}, key={}, reason={}",
                            caseId, f.getFileId(), f.getFilePath(), e.getMessage(), e);
                }

                InvestigationFile item = InvestigationFile.builder()
                        .analysisId(analysis.getAnalysisId())
                        .fileType(f.getFileType() == null ? null : f.getFileType().name())
                        .originName(f.getOriginName())
                        .presignedUrl(presignedUrl)
                        .contentType(contentType)
                        .build();

                invFiles.add(item);
            }

            // Legal Criteria 가져오기 (Case 관련) + LegalIssue 포함해서 AI DTO로 변환
            List<CriteriaEntity> legalCriteriaEntities = criteriaRepository.findAllByLegalIssue_CaseEntity_CaseIdOrderByCreatedAtAsc(caseId);

            List<LegalCriteria> aiLegalCriteria = legalCriteriaEntities.stream()
                    .map(lc -> {
                        LegalIssueEntity legalIssue = lc.getLegalIssue();

                        LegalIssue aiIssue = LegalIssue.builder()
                                .issueName(legalIssue.getIssueName())
                                .relatedLaw(legalIssue.getRelatedLaw())
                                .build();

                        return LegalCriteria.builder()
                                .criterionText(lc.getCriterionText())
                                .precedentCaseNo(lc.getPrecedentCaseNo())
                                .precedentDecisionDate(lc.getPrecedentDecisionDate())
                                .legalIssue(aiIssue)
                                .build();
                    }).toList();

            FileAnalysisRequest req = FileAnalysisRequest.builder()
                    .caseDetail(caseDetail)
                    .investigationFiles(invFiles)
                    .legalCriteria(aiLegalCriteria)
                    .build();

            FileAnalysisResponse resp = aiClient.uploadInvestigationFile(req);

            log.info("AI 자료파일 분석 요청이 완료되었습니다. caseId={}, status={}", caseId, resp == null ? null : resp.getStatus());

        } catch (AiClientException e) {
            log.warn("AI 자료파일 분석 요청에 실패했습니다. caseId={}, status={}, body={}",
                    caseId, e.getStatusCode(), e.getResponseBody(), e);
        } catch (CustomException e) {
            log.warn("AI 자료파일 분석 요청이 생략되었습니다. caseId={}, reason={}", caseId, e.getMessage(), e);
        } catch (Exception e) {
            log.warn("AI 자료파일 분석 요청 과정에서 문제가 문제가 발생했습니다. caseId={}, reason={}", caseId, e.getMessage(), e);
        }
    }

    @Transactional
    public void handleStageCallback(UUID caseId, UUID analysisId, AiCallbackRequest request) {
        // analysis 존재 여부 확인 + 사건 매칭 검증
        AnalysisEntity analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_NOT_FOUND));

        if (!analysis.getFile().getCaseId().equals(caseId)) {
            throw new CustomException(ErrorCode.CASE_NOT_FOUND); // 또는 별도 에러코드
        }

        // stage 분기
        switch (request.getStage()) {
            case SUMMARY -> handleSummary(analysis, request.getPayload());
            case TRANSCRIPT -> handleTranscript(analysis, request.getPayload());
            case KEY_STATEMENT -> handleKeyStatement(analysis, request.getPayload());
            case CONTRADICTION -> handleContradiction(caseId, analysis, request.getPayload());
            case QUESTION -> handleQuestion(analysis, request.getPayload());
            case VIDEO_EVENT -> handleVideoEvent(analysis, request.getPayload());
            default -> throw new CustomException(ErrorCode.INVALID_STAGE_TYPE);
        }

        // 2) (선택) 필수 stage 모두 수신되면 COMPLETED 전환 로직
        // - stage 수신 여부를 analysis.resultData(json)로 기록하거나,
        // - 별도 “analysis_stage_received” 테이블을 두는 방식 중 택1
    }

    private void handleSummary(AnalysisEntity analysis, JsonNode payloadNode) {
        SummaryPayload payload = objectMapper.treeToValue(payloadNode, SummaryPayload.class);

        analysis.setResultData(payload.toString()); // JSON 문자열 저장
        analysis.setStatus(AnalysisStatus.COMPLETED);

        analysisRepository.save(analysis);
    }

    private void handleTranscript(AnalysisEntity analysis, JsonNode payloadNode) {
        TranscriptPayload payload = objectMapper.treeToValue(payloadNode, TranscriptPayload.class);

        for (TranscriptPayload.TranscriptLine line : payload.getContent()) {
            TranscriptEntity transcript = TranscriptEntity.builder()
                    .analysis(analysis)
                    .speaker(line.getSpeaker())
                    .text(line.getContent())
                    .build();
            transcriptRepository.save(transcript);
        }
        log.info("Transcript 저장 완료. AnalysisId={}, Transcript Entity 개수={}", analysis.getAnalysisId(), payload.getContent().size());
    }

    private void handleKeyStatement(AnalysisEntity analysis, JsonNode payloadNode) {
        if (!payloadNode.isArray()) throw new CustomException(ErrorCode.INVALID_KEY_STATEMENT_TYPE);

        List<KeyStatementPayload> payloads = new ArrayList<>();
        for (JsonNode n : payloadNode) {
            payloads.add(objectMapper.treeToValue(n, KeyStatementPayload.class));
        }

        for (KeyStatementPayload payload : payloads) {
            KeyStatementEntity.builder()
                    // ===========================
                    // 수정 필요
                    // ===========================
                    .transcript(null)
                    .statementText(null)
                    // ===========================
                    .description(payload.getContent())
                    .build();
        }
        // transcript를 FK로 잡는 구조면 transcript 먼저 찾고 연결해야 함.
        // 지금 코드엔 transcriptRepository가 있으니 가장 최신 transcript를 연결하는 등 정책 필요.
    }

    private void handleContradiction(UUID caseId, AnalysisEntity analysis, JsonNode payloadNode) {
        if (!payloadNode.isArray()) throw new CustomException(ErrorCode.INVALID_CONTRADICTION_TYPE);

        CaseEntity caseEntity = caseRepository.findById(caseId).orElseThrow(
                () -> new CustomException(ErrorCode.CASE_NOT_FOUND)
        );

        List<ContradictionPayload> payloads = new ArrayList<>();
        for (JsonNode n : payloadNode) {
            payloads.add(objectMapper.treeToValue(n, ContradictionPayload.class));
        }

        for (ContradictionPayload payload : payloads) {
            ContradictionEntity.builder()
                    // ===========================
                    // 수정 필요
                    // ===========================
                    .caseEntity(caseEntity)
                    .statement1(null) // statementTempId 찾아서 넣기
                    .statement2(null) // counterStatementTempId 찾아서 넣기
                    // ===========================
                    .type(payload.getType())
                    .status(ContradictionStatus.UNRESOLVED)
                    .description(payload.getDescription())
                    .build();
        }
        // transcript를 FK로 잡는 구조면 transcript 먼저 찾고 연결해야 함.
        // 지금 코드엔 transcriptRepository가 있으니 가장 최신 transcript를 연결하는 등 정책 필요.
    }


    private void handleQuestion(AnalysisEntity analysis, JsonNode payloadNode) {
        if (!payloadNode.isArray()) throw new CustomException(ErrorCode.INVALID_QUESTION_TYPE);

        List<QuestionPayload> payloads = new ArrayList<>();
        for (JsonNode n : payloadNode) {
            payloads.add(objectMapper.treeToValue(n, QuestionPayload.class));
        }

        for (QuestionPayload payload : payloads) {
            QuestionEntity.builder()
                    // ===========================
                    // 수정 필요
                    // ===========================
                    .contradiction(null)
                    .content(payload.getQuestion())
                    .rationale(null)
                    // ===========================
                    .build();
        }
        // contradiction id를 FK로 잡는 구조면 contradiction 먼저 찾고 연결해야 함.
    }

    private void handleVideoEvent(AnalysisEntity analysis, JsonNode payloadNode) {
        if (!payloadNode.isArray()) throw new CustomException(ErrorCode.INVALID_VIDEO_EVENT_TYPE);

        List<VideoEventPayload> payloads = new ArrayList<>();
        for (JsonNode n : payloadNode) {
            payloads.add(objectMapper.treeToValue(n, VideoEventPayload.class));
        }

        for (VideoEventPayload payload : payloads) {
            VideoEventEntity.builder()
                    .analysisId(analysis.getAnalysisId())
                    .startTime(payload.getStartTime())
                    .endTime(payload.getEndTime())
                    .eventType(payload.getEventType())
                    .eventDescription(payload.getEventDescription())
                    .build();
        }
    }

    private void mergeStageResult(AnalysisEntity analysis, AnalysisStage stage, JsonNode payloadNode) {
        try {
            ObjectNode root;

            if (analysis.getResultData() == null || analysis.getResultData().isBlank()) {
                root = objectMapper.createObjectNode();
            } else {
                JsonNode existing = objectMapper.readTree(analysis.getResultData());
                root = existing != null && existing.isObject()
                        ? (ObjectNode) existing
                        : objectMapper.createObjectNode();
            }

            root.set(stage.name(), payloadNode); // stage별로 덮어쓰기(같은 stage가 여러 번 오면 최신값)
            analysis.setResultData(root.toString());

        } catch (Exception e) {
            throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
        }
    }
}