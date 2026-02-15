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
import com.aivle.big_project.contradiction.repository.ContradictionRepository;
import com.aivle.big_project.criteria.entity.CriteriaEntity;
import com.aivle.big_project.criteria.entity.LegalIssueEntity;
import com.aivle.big_project.criteria.repository.CriteriaRepository;
import com.aivle.big_project.criteria.service.CriteriaEvaluationService;
import com.aivle.big_project.investigationFile.entity.InvestigationFileEntity;
import com.aivle.big_project.investigationFile.repository.InvestigationFileRepository;
import com.aivle.big_project.keyStatement.entity.KeyStatementEntity;
import com.aivle.big_project.keyStatement.repository.KeyStatementRepository;
import com.aivle.big_project.question.entity.QuestionEntity;
import com.aivle.big_project.question.repository.QuestionRepository;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;
import java.util.*;

import static com.aivle.big_project.analysis.entity.AnalysisStatus.COMPLETED;

@Service @Slf4j
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final KeyStatementRepository keyStatementRepository;
    private final VideoEventRepository videoEventRepository;
    private final TranscriptRepository transcriptRepository;

    private final CaseRepository caseRepository;
    private final InvestigationFileRepository investigationFileRepository;
    private final CriteriaRepository criteriaRepository;
    private final ObjectStorageService objectStorageService;
    private final CriteriaEvaluationService criteriaEvaluationService;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final ContradictionRepository contradictionRepository;
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 분석 상태 조회
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public AnalysisDetailResponse getAnalysisDetail(UUID analysisId) {
        AnalysisEntity analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_NOT_FOUND));

        if (analysis.getStatus() != COMPLETED) {
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
                    .occurredAt(aCase.getOccurredAt().toString())
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
//            List<CriteriaEntity> legalCriteriaEntities = criteriaRepository.findAllByLegalIssue_CaseEntity_CaseIdOrderByCreatedAtAsc(caseId);

            List<CriteriaEntity> legalCriteriaEntities = criteriaRepository.findAllWithLegalIssueByLegalIssue_CaseEntity_CaseIdOrderByCreatedAtAsc(caseId);

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
        log.info("==handleStageCallback Start== caseId={}, analysisId={}, request={}", caseId, analysisId, request.toString());
        // analysis 존재 여부 확인 + 사건 매칭 검증
//        AnalysisEntity analysis = analysisRepository.findById(analysisId)
        AnalysisEntity analysis = analysisRepository.findByIdForUpdate(analysisId)
                .orElseThrow(() -> new CustomException(ErrorCode.ANALYSIS_NOT_FOUND));
        boolean wasCompleted = analysis.getStatus() == COMPLETED;

        // 이미 COMPLETED 된 분석에 대한 추가 콜백 처리
        // - VIDEO_EVENT는 여러 번 올 수 있음
        // - AUDIO는 빈 CONTRADICTION+QUESTION으로 먼저 COMPLETED될 수 있음
        //   COMPLETED 이후에도 CONTRADICTION/QUESTION 저장 허용 (중복은 temp_map으로 방지)
        if (wasCompleted) {
            if (!analysis.getFile().getCaseId().equals(caseId)) {
                log.warn("요청 analysis_id 와 case_id의 경로가 맞지 않습니다. 1");
                throw new CustomException(ErrorCode.ANALYSIS_NOT_IN_CASE);
            }

            if (request.getStage() == AnalysisStage.VIDEO_EVENT) {
                handleVideoEvent(analysis, request.getPayload());
            } else if (request.getStage() == AnalysisStage.CONTRADICTION) {
                handleContradiction(caseId, analysis, request.getPayload());
            } else if (request.getStage() == AnalysisStage.QUESTION) {
                handleQuestion(caseId, analysis, request.getPayload());
            }
            mergeStageResultWithMeta(analysis, request.getStage(), request.getPayload());

            analysisRepository.saveAndFlush(analysis); // save -> saveAndFlush
            log.info("COMPLETED 이후 콜백 처리. caseId={}, analysisId={}, stage={}",
                    caseId, analysisId, request.getStage());
            log.info("==handleStageCallback End== caseId={}, analysisId={}, request={}", caseId, analysisId, request.toString());
            return;
        }

        // 분석 결과 = 사건 연결되어 있는지 검증
        if (!analysis.getFile().getCaseId().equals(caseId)) {
            log.warn("요청 analysis_id 와 case_id의 경로가 맞지 않습니다. 2");
            throw new CustomException(ErrorCode.ANALYSIS_NOT_IN_CASE);
        }

        // stage 분기
        switch (request.getStage()) {
            case SUMMARY -> handleSummary(analysis, request.getPayload());
            case TRANSCRIPT -> handleTranscript(analysis, request.getPayload());
            case KEY_STATEMENT -> handleKeyStatement(analysis, request.getPayload());
            case CONTRADICTION -> handleContradiction(caseId, analysis, request.getPayload());
            case QUESTION -> handleQuestion(caseId, analysis, request.getPayload());
            case VIDEO_EVENT -> handleVideoEvent(analysis, request.getPayload());
            default -> throw new CustomException(ErrorCode.INVALID_STAGE_TYPE);
        }

        // stage 결과를 analysis.resultData(JSON)에 누적 저장 + 메타(last_received_at, received 플래그) 갱신
        mergeStageResultWithMeta(analysis, request.getStage(), request.getPayload());
        // 완료 처리(파일 1개당 1회만): 이미 COMPLETED 된 Analysis 면 아무것도 하지 않는다.

        FileType ft = analysis.getFile().getFileType();

            // DOCUMENT/IMAGE: SUMMARY 수신 시 완료
        if ((ft == FileType.DOCUMENT || ft == FileType.IMAGE) && request.getStage() == AnalysisStage.SUMMARY) {
            analysis.setStatus(COMPLETED);
            analysisRepository.saveAndFlush(analysis); // save -> saveAndFlush
            log.info("analysisRepository.saveAndFlush(analysis) -> Completed (DOCUMENT/IMAGE SUMMARY)");
//            criteriaEvaluationService.reevaluateAllByAnalysis(caseId, analysisId);
            publishReevaluateAfterCommit(caseId, analysisId);
            log.info("==handleStageCallback End== caseId={}, analysisId={}, request={}", caseId, analysisId, request.toString());
            return;
        }

        // VIDEO: VIDEO_EVENT 수신 시 완료
        if (ft == FileType.VIDEO && request.getStage() == AnalysisStage.VIDEO_EVENT) {
            analysis.setStatus(COMPLETED);
            analysisRepository.saveAndFlush(analysis); // save -> saveAndFlush
            log.info("analysisRepository.saveAndFlush(analysis) -> Completed (VIDEO VIEDO_EVENT)");
//            criteriaEvaluationService.reevaluateAllByAnalysis(caseId, analysisId);
            publishReevaluateAfterCommit(caseId, analysisId);
            log.info("==handleStageCallback End== caseId={}, analysisId={}, request={}", caseId, analysisId, request.toString());
            return;
        }

        // AUDIO: QUESTION 수신 시 완료 (빈 payload로 온 경우에도 이후 콜백 올 수 있으므로 처리)
        if (ft == FileType.AUDIO && request.getStage() == AnalysisStage.QUESTION) {
            boolean payloadNonEmpty = request.getPayload() != null && request.getPayload().isArray() && request.getPayload().size() > 0;

            // __meta.received.CONTRADICTION 여부 (이미 들어온 모순이 있을 떄만 QUESTION을 완료 트리거로 취급)
            boolean hasContradictionFlag = false;
            try {
                ObjectNode root = readOrCreateRoot(analysis);
                ObjectNode meta = getOrCreateMeta(root);
                JsonNode received = meta.get("received");
                hasContradictionFlag = received != null && received.isObject() && received.has("CONTRADICTION");
            } catch (Exception ignore) {}

            if(payloadNonEmpty || hasContradictionFlag) {
                analysis.setStatus(COMPLETED);
                analysisRepository.saveAndFlush(analysis);

                log.info("analysisRepository.saveAndFlush(analysis) -> Completed (VIDEO VIEDO_EVENT)");
                publishReevaluateAfterCommit(caseId, analysisId);
                log.info("==handleStageCallback End== caseId={}, analysisId={}, request={}", caseId, analysisId, request.toString());
                return;
            }
            // payload가 빈 채로 QUESTION stage까지 요청이 오므로 일단 처리
            analysis.setStatus(COMPLETED);
            analysisRepository.saveAndFlush(analysis);
            log.info("analysisRepository.saveAndFlush(analysis) -> Completed (AUDIO QUESTION)");
            publishReevaluateAfterCommit(caseId, analysisId);
            log.info("==handleStageCallback End== caseId={}, analysisId={}, request={}", caseId, analysisId, request.toString());
            return;
        }

        // AUDIO 타임아웃 완료는 스케줄러가 처리한다.
        analysisRepository.saveAndFlush(analysis);
        log.info("analysisRepository.saveAndFlush(analysis) -> saved");
        log.info("==handleStageCallback End== caseId={}, analysisId={}, request={}", caseId, analysisId, request.toString());
    }

    private void handleSummary(AnalysisEntity analysis, JsonNode payloadNode) {
        // SUMMARY는 handleStageCallback()에서 공통으로 처리
        // payload 스키마 검증만 수행
        objectMapper.treeToValue(payloadNode, SummaryPayload.class);
    }

    private void handleTranscript(AnalysisEntity analysis, JsonNode payloadNode) {
        // 명세: payload는 배열([{temp_id, speaker, content}, ...])
        if (!payloadNode.isArray()) {
            throw new CustomException(ErrorCode.INVALID_TRANSCRIPT_TYPE);
        }
        ObjectNode root = readOrCreateRoot(analysis);
        ObjectNode meta = getOrCreateMeta(root);

        int saved = 0;
        for (JsonNode n : payloadNode) {
            String tempId = n.hasNonNull("temp_id") ? n.get("temp_id").asText() : null;
            String speaker = n.hasNonNull("speaker") ? n.get("speaker").asText() : null;
            String content = n.hasNonNull("content") ? n.get("content").asText() : null;

            TranscriptEntity transcript = TranscriptEntity.builder()
                    .analysis(analysis)
                    .speaker(speaker)
                    .text(content)
                    .build();

            TranscriptEntity savedEntity = transcriptRepository.save(transcript);
            saved++;

            // temp_id -> transcriptId(UUID) 매핑 저장
            if (tempId != null && !tempId.isBlank()) {
                putTempMap(meta, "TRANSCRIPT", tempId, savedEntity.getTranscriptId());
            }
        }
        // temp_map 반영
        analysis.setResultData(root.toString());
        log.info("Transcript 저장 완료. AnalysisId={}, Transcript Entity 개수={}", analysis.getAnalysisId(), saved);
    }

    private void handleKeyStatement(AnalysisEntity analysis, JsonNode payloadNode) {
        if (!payloadNode.isArray()) throw new CustomException(ErrorCode.INVALID_KEY_STATEMENT_TYPE);

        List<KeyStatementPayload> payloads = new ArrayList<>();
        for (JsonNode n : payloadNode) {
            payloads.add(objectMapper.treeToValue(n, KeyStatementPayload.class));
        }

        ObjectNode root = readOrCreateRoot(analysis);
        ObjectNode meta = getOrCreateMeta(root);

        for (KeyStatementPayload payload : payloads) {

            // transcript_id는 TRANSCRIPT stage의 temp_id를 참조
            UUID transcriptId = resolveTempMap(meta, "TRANSCRIPT", payload.getTranscriptId());
            if (transcriptId == null) {
                throw new CustomException(ErrorCode.TRANSCRIPT_NOT_FOUND);
            }

            TranscriptEntity transcript = transcriptRepository.findById(transcriptId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TRANSCRIPT_NOT_FOUND));

            KeyStatementEntity keyStatement = KeyStatementEntity.builder()
                    .transcript(transcript)
                    .statementText(payload.getStatementText())
                    .description(payload.getContent())
                    .build();

            KeyStatementEntity savedEntity = keyStatementRepository.save(keyStatement);

            // KEY_STATEMENT temp_id -> statementId(UUID) 매핑 저장
            if (payload.getTempId() != null && !payload.getTempId().isBlank()) {
                putTempMap(meta, "KEY_STATEMENT", payload.getTempId(), savedEntity.getStatementId());
            }
        }
        // temp_map 반영
        analysis.setResultData(root.toString());
    }

    private void handleContradiction(UUID caseId, AnalysisEntity analysis, JsonNode payloadNode) {
        if (!payloadNode.isArray()) throw new CustomException(ErrorCode.INVALID_CONTRADICTION_TYPE);

        CaseEntity caseEntity = caseRepository.findById(caseId).orElseThrow(
                () -> new CustomException(ErrorCode.CASE_NOT_FOUND)
        );

        ObjectNode root = readOrCreateRoot(analysis);
        ObjectNode meta = getOrCreateMeta(root);

        for (JsonNode n : payloadNode) {
            String tempId = n.hasNonNull("temp_id") ? n.get("temp_id").asString() : null;
            String raw1 = n.hasNonNull("statement_id_1") ? n.get("statement_id_1").asString() : null;
            String raw2 = n.hasNonNull("statement_id_2") ? n.get("statement_id_2").asString() : null;
            String type = n.hasNonNull("type") ? n.get("type").asString() : null;
            String description = n.hasNonNull("description") ? n.get("description").asString() : null;

            if (tempId != null && !tempId.isBlank()) {
                UUID already = resolveTempMap(meta, "CONTRADICTION", tempId);
                if (already != null) {
                    continue;
                }
            }
//            UUID st1Id = resolveTempMap(meta, "KEY_STATEMENT", payload.getStatementId1());
//            UUID st2Id = resolveTempMap(meta, "KEY_STATEMENT", payload.getStatementId2());
            UUID st1Id = resolveKeyStatementIdAcrossCase(caseId, analysis, raw1);
            UUID st2Id = resolveKeyStatementIdAcrossCase(caseId, analysis, raw2);

            if (st1Id == null || st2Id == null) {
                log.warn("KeyStatement 참조 해석 실패. analysisId={}, raw1={}, raw2={} (case-scan 포함)",
                        analysis.getAnalysisId(), raw1, raw2);
                throw new CustomException(ErrorCode.KEY_STATEMENT_NOT_FOUND);
            }

            KeyStatementEntity st1 = keyStatementRepository.findById(st1Id)
                    .orElseThrow(() -> new CustomException(ErrorCode.KEY_STATEMENT_NOT_FOUND));
            KeyStatementEntity st2 = keyStatementRepository.findById(st2Id)
                    .orElseThrow(() -> new CustomException(ErrorCode.KEY_STATEMENT_NOT_FOUND));

            ContradictionEntity contradiction = ContradictionEntity.builder()
                    .caseEntity(caseEntity)
                    .statement1(st1)
                    .statement2(st2)
                    .type(type)
                    .status(ContradictionStatus.UNRESOLVED)
                    .description(description)
                    .build();

            ContradictionEntity savedEntity = contradictionRepository.save(contradiction);

            // CONTRADICTION temp_id -> contradictionId(UUID) 매핑 저장
            if (tempId != null && !tempId.isBlank()) {
                putTempMap(meta, "CONTRADICTION", tempId, savedEntity.getContradictionId());
            }
        }
        analysis.setResultData(root.toString());
    }


    private void handleQuestion(UUID caseId, AnalysisEntity analysis, JsonNode payloadNode) {
        if (!payloadNode.isArray()) throw new CustomException(ErrorCode.INVALID_QUESTION_TYPE);

        ObjectNode root = readOrCreateRoot(analysis);
        ObjectNode meta = getOrCreateMeta(root);

        for (JsonNode n : payloadNode) {
            String tempId = n.hasNonNull("temp_id") ? n.get("temp_id").asString() : null;
            String contradictionRef = n.hasNonNull("contradiction_id") ? n.get("contradiction_id").asString() : null;
            String target = n.hasNonNull("target") ? n.get("target").asString() : null;
            String questionText = n.hasNonNull("question") ? n.get("question").asString() : null;
            String rationale = n.hasNonNull("rationale") ? n.get("rationale").asString() : null;

            // idempotency : 동일 temp_id가 이미 매핑되어 있는 경우 skip
            if (tempId != null && !tempId.isBlank()) {
                UUID already = resolveTempMap(meta, "QUESTION", tempId);
                if (already != null) {
                    continue;
                }
            }
            UUID contradictionId = resolveContradictionIdAcrossCase(caseId, analysis, contradictionRef);

            if (contradictionId == null) {
                throw new CustomException(ErrorCode.CONTRADICTION_NOT_FOUND);
            }

            ContradictionEntity contradiction = contradictionRepository.findById(contradictionId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CONTRADICTION_NOT_FOUND));

            QuestionEntity question = QuestionEntity.builder()
                    .contradiction(contradiction)
                    .content(questionText)
                    .rationale(rationale)
                    .build();

            QuestionEntity saved = questionRepository.save(question);

            if (tempId != null && !tempId.isBlank()) {
                putTempMap(meta, "QUESTION", tempId, saved.getQuestionId());
            }
        }
        analysis.setResultData(root.toString());
    }

    private void handleVideoEvent(AnalysisEntity analysis, JsonNode payloadNode) {
        if (!payloadNode.isArray()) throw new CustomException(ErrorCode.INVALID_VIDEO_EVENT_TYPE);

        List<VideoEventPayload> payloads = new ArrayList<>();
        for (JsonNode n : payloadNode) {
            payloads.add(objectMapper.treeToValue(n, VideoEventPayload.class));
        }

        for (VideoEventPayload payload : payloads) {
            VideoEventEntity videoEvent = VideoEventEntity.builder()
                    .analysisId(analysis.getAnalysisId())
                    .startTime(payload.getStartTime())
                    .endTime(payload.getEndTime())
                    .eventType(payload.getEventType())
                    .eventDescription(payload.getEventDescription())
                    .build();
            videoEventRepository.save(videoEvent);
        }
    }

    /**
     * stage 결과를 resultData(JSON)에 누적 저장하면서,
     * - __meta.last_received_at: 마지막 콜백 수신 시각(ISO)
     * - 플래그__meta.received: stage 수신 여부
     * 를 함께 기록한다.
     * 명세 형태: result_data: [{stage, payload}, ...]
     */
    private void mergeStageResultWithMeta(AnalysisEntity analysis, AnalysisStage stage, JsonNode payloadNode) {
        try {
            ObjectNode root = readOrCreateRoot(analysis);

            // meta 영역
            ObjectNode meta = getOrCreateMeta(root);
            meta.put("last_received_at", LocalDateTime.now().toString());

            ObjectNode received;
            JsonNode receivedNode = meta.get("received");
            if (receivedNode != null && receivedNode.isObject()) {
                received = (ObjectNode) receivedNode;
            } else {
                received = objectMapper.createObjectNode();
                meta.set("received", received);
            }
            received.put(stage.name(), true);

            // stage payload 저장(누적) - 명세 형태: result_data: [{stage, payload}, ...]
            ArrayNode resultData;
            JsonNode rdNode = root.get("result_data");
            if (rdNode != null && rdNode.isArray()) {
                resultData = (ArrayNode) rdNode;
            } else {
                resultData = objectMapper.createArrayNode();
                root.set("result_data", resultData);
            }

            ObjectNode item = objectMapper.createObjectNode();
            item.put("stage", stage.name());
            item.set("payload", payloadNode);
            resultData.add(item);

            analysis.setResultData(root.toString());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
        }
    }

    /**
     * AUDIO 분석 완료 타임아웃 처리:
     * - TRANSCRIPT/KEY_STATEMENT는 반드시 온다.
     * - CONTRADICTION/QUESTION은 없을 수 있다.
     * - 마지막 콜백 수신 후 일정 시간 동안 CONTRADICTION이 없으면 COMPLETED로 마감한다.
     *
     * 주의: @EnableScheduling이 애플리케이션에 활성화되어 있어야 한다.
     */
    @Scheduled(fixedDelay = 15000) // 15초마다 체크
    @Transactional
    public void finalizeAudioAnalysesByTimeout() {
        // COMPLETED 제외 전체를 가져와 메모리에서 필터
        List<AnalysisEntity> all =
                analysisRepository.findAllByStatusNotAndFile_FileType(AnalysisStatus.COMPLETED, FileType.AUDIO);

        LocalDateTime now = LocalDateTime.now();
        int finalized = 0;

        for (AnalysisEntity analysis : all) {
            if (analysis.getStatus() == COMPLETED) continue;
            if (analysis.getFile() == null || analysis.getFile().getFileType() != FileType.AUDIO) continue;
            if (analysis.getResultData() == null || analysis.getResultData().isBlank()) continue;

            try {
                JsonNode root = objectMapper.readTree(analysis.getResultData());
                JsonNode meta = root.get("__meta");
                if (meta == null || !meta.isObject()) continue;

                JsonNode lastNode = meta.get("last_received_at");
                if (lastNode == null || !lastNode.isTextual()) continue;

                LocalDateTime last = LocalDateTime.parse(lastNode.asText());
                // 30초 이상 추가 콜백이 없으면 타임아웃으로 마감
                if (last.plusSeconds(30).isAfter(now)) continue;

                JsonNode received = meta.get("received");
                if (received == null || !received.isObject()) continue;

                boolean hasTranscript = received.has("TRANSCRIPT");
                boolean hasKeyStatement = received.has("KEY_STATEMENT");
                boolean hasContradiction = received.has("CONTRADICTION");
                boolean hasQuestion = received.has("QUESTION");

                // 기본 요건(TRANSCRIPT + KEY_STATEMENT)이 없으면 아직 마감 불가
                if (!hasTranscript || !hasKeyStatement) continue;

                // CONTRADICTION이 있는데 QUESTION이 없는 경우
                // -> 일정 시간 후, 추가 콜백 없을 시 상태 변경(COMPLETE) 및 법리기준충족평가 처리
                if (hasContradiction && !hasQuestion) {
                    analysis.setStatus(COMPLETED);
                    analysisRepository.save(analysis);
                    UUID caseId = analysis.getFile().getCaseId();
                    publishReevaluateAfterCommit(caseId, analysis.getAnalysisId());
                    finalized++;
                    continue;
                }

                // CONTRADICTION이 없다면(즉, 질문 생성 단계로 갈 필요 없음) 타임아웃으로 완료 처리
                if (!hasContradiction) {
                    analysis.setStatus(COMPLETED);
                    analysisRepository.save(analysis);

                    // 법리 기준 재평가(파일 1개당 1회)
                    UUID caseId = analysis.getFile().getCaseId();
//                    criteriaEvaluationService.reevaluateAllByAnalysis(caseId, analysis.getAnalysisId());
                    publishReevaluateAfterCommit(caseId, analysis.getAnalysisId());
                    finalized++;
                }
            } catch (Exception ignored) {
                // resultData 파싱 실패 시 스킵
            }
        }

        if (finalized > 0) {
            log.info("AUDIO 분석 타임아웃 마감 처리 완료. count={}", finalized);
        }
    }

    /**
     * 재평가는 반드시 콜백 트랜잭션 커밋 이후에 실행되어야 한다.
     * (result_data / __meta 등이 DB에 반영되기 전에 reevaluate가 먼저 수행되는 문제 방지)
     */
    private void publishReevaluateAfterCommit(UUID caseId, UUID analysisId) {
        try {
//            criteriaEvaluationService.reevaluateAllByAnalysis(caseId, analysisId);
            eventPublisher.publishEvent(new ReevaluateRequestEvent(caseId, analysisId));
        } catch (Exception e) {
            // 이벤트 발행 실패는 콜백 저장을 막지 않는다.
            log.warn("Reevaluate 이벤트 발행 실패(콜백 저장은 유지). caseId={}, analysisId={}, reason={}",
                    caseId, analysisId, e.getMessage(), e);

        }
    }

    /** 콜백 저장 이후(after-commit) 실행을 위한 이벤트 */
    private record ReevaluateRequestEvent(UUID caseId, UUID analysisId) {}

    /**
     * 트랜잭션 커밋 이후에, 비동기로 법리 기준을 재평가
     * - 콜백 응답 지연을 줄이고(AI 서버 timeout 방지)
     * - DB에 저장된 최신 result_data를 기반으로 재평가가 수행되도록 함
     */
    @TransactionalEventListener(
            phase = org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT)
    @Async
    public void onReevaluateAfterCommit(ReevaluateRequestEvent event) {
        try {
            criteriaEvaluationService.reevaluateAllByAnalysis(event.caseId(), event.analysisId());
        } catch (Exception e) {
            log.warn("Criteria Reevaluate 실패(콜백 저장은 유지). caseId={}, analysisId={}, reason={}",
                    event.caseId(), event.analysisId(), e.getMessage(), e);
        }
    }


    // Analysis 엔티티에서 result_data 가져오기
    private ObjectNode readOrCreateRoot(AnalysisEntity analysis) {
        try {
            if (analysis.getResultData() == null || analysis.getResultData().isBlank()) {
                return objectMapper.createObjectNode();
            }
            JsonNode existing = objectMapper.readTree(analysis.getResultData());
            return existing != null && existing.isObject() ? (ObjectNode) existing : objectMapper.createObjectNode();
        } catch (Exception e) {
            return objectMapper.createObjectNode();
        }
    }
    // Analysis 엔티티에서 result_data 아래 메타데이터 생성
    private ObjectNode getOrCreateMeta(ObjectNode root) {
        JsonNode metaNode = root.get("__meta");
        if (metaNode != null && metaNode.isObject()) return (ObjectNode) metaNode;
        ObjectNode meta = objectMapper.createObjectNode();
        root.set("__meta", meta);
        return meta;
    }

    // AnalysisEntity - resultData - 내부 메타데이터에 temp_map 생성 + tempId-UUID 매핑
    private void putTempMap(ObjectNode meta, String group, String tempId, UUID realId) {
        if (tempId == null || tempId.isBlank() || realId == null) return;

        ObjectNode tempMap = meta.has("temp_map") && meta.get("temp_map").isObject()
                ? (ObjectNode) meta.get("temp_map")
                : objectMapper.createObjectNode();
        meta.set("temp_map", tempMap);

        ObjectNode groupNode = tempMap.has(group) && tempMap.get(group).isObject()
                ? (ObjectNode) tempMap.get(group)
                : objectMapper.createObjectNode();
        tempMap.set(group, groupNode);

        groupNode.put(tempId, realId.toString());
    }

    // AnalysisEntity - resultData - 내부 메타데이터에서 tempId->UUID 탐색 및 변환
    private UUID resolveTempMap(ObjectNode meta, String group, String tempId) {
        if (tempId == null || tempId.isBlank()) return null;
        JsonNode tm = meta.get("temp_map");
        if (tm == null || !tm.isObject()) return null;
        JsonNode g = tm.get(group); // 스테이지 가져오기
        if (g == null || !g.isObject()) return null;
        JsonNode v = g.get(tempId); // 스테이지에서 tempId에 해당하는 실제 UUID 꺼내기
        if (v == null || !v.isTextual()) return null;
        try { return UUID.fromString(v.asText()); } // UUID return
        catch (Exception e) { return null; }
    }

    // KEY_STATEMENT 참조 해석
    // 현재 analysis의 __meta.temp_map(KEY_STATEMENT)에서 조회
    // 같은 case의 다른 analysis들의 result_data.__meta.temp_map(KEY_STATEMENT)까지 스캔
    private UUID resolveKeyStatementIdAcrossCase(UUID caseId, AnalysisEntity current, String ref) {
        if (ref == null || ref.isBlank()) return null;

        try {
            ObjectNode root = readOrCreateRoot(current);
            ObjectNode meta = getOrCreateMeta(root);
            UUID v = resolveTempMap(meta, "KEY_STATEMENT", ref);
            if (v != null ) return v;
        } catch (Exception ignore) {}

        try {
            List<AnalysisEntity> analyses = analysisRepository.findAllByFile_CaseId(caseId);
            for (AnalysisEntity a : analyses) {
                if (a == null || a.getResultData() == null || a.getResultData().isBlank()) continue;
                ObjectNode root = readOrCreateRoot(a);
                ObjectNode meta = getOrCreateMeta(root);
                UUID v = resolveTempMap(meta, "KEY_STATEMENT", ref);
                if (v != null ) return v;
            }
        } catch (Exception ignore) {}

        return null;
    }

    // CONTRADICTION 참조 해석
    // QUESTION이 다른 analysis로 라우팅되는 경우를 대비해 case 전체 스캔
    private UUID resolveContradictionIdAcrossCase(UUID caseId, AnalysisEntity current, String ref) {
        if (ref == null || ref.isBlank()) return null;
        try {
            ObjectNode root = readOrCreateRoot(current);
            ObjectNode meta = getOrCreateMeta(root);
            UUID v = resolveTempMap(meta, "CONTRADICTION", ref);
            if (v != null ) return v;
        } catch (Exception ignore) {}
        try {
            List<AnalysisEntity> analyses = analysisRepository.findAllByFile_CaseId(caseId);
            for (AnalysisEntity a : analyses) {
                if (a == null || a.getResultData() == null || a.getResultData().isBlank()) continue;
                ObjectNode root = readOrCreateRoot(a);
                ObjectNode meta = getOrCreateMeta(root);
                UUID v = resolveTempMap(meta, "CONTRADICTION", ref);
                if (v != null ) return v;
            }
        } catch (Exception ignore) {}

        return null;
    }
}