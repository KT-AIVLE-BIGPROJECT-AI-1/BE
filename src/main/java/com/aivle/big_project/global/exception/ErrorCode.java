package com.aivle.big_project.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // auth
    EMAIL_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "이미 사용 중인 이메일입니다."
    ),

    INVALID_EMAIL(
            HttpStatus.BAD_REQUEST,
            "올바르지 않은 이메일입니다."
    ),

    INVALID_REFRESH_TOKEN(
            HttpStatus.BAD_REQUEST,
            "유효하지 않은 Refresh Token입니다."
    ),

    EXPIRED_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "토큰이 만료되었습니다."
    ),

    INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "토큰이 유효하지 않습니다."
    ),

    AUTHORIZATION_REQUIRED(
            HttpStatus.UNAUTHORIZED,
            "인증이 필요합니다."
    ),

    INVALID_AUTHORIZATION_INFO(
            HttpStatus.UNAUTHORIZED,
            "올바르지 않은 인증 정보입니다."
    ),

    REFRESH_TOKEN_HASH_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "refresh token 해싱에 실패했습니다."
    ),

    ROLE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "역할이 설정되어 있지 않습니다."
    ),

    // admin
    JURISDICTION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "관리자 계정의 '관할서' 항목이 설정되어 있지 않습니다."
    ),

    ADMIN_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "관리자를 찾을 수 없습니다."
    ),

    TARGET_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "감사 대상 사용자를 찾을 수 없습니다."
    ),

    INVALID_JURISDICTION_STATION(
            HttpStatus.BAD_REQUEST,
            "다른 관할서 사용자의 로그는 조회할 수 없습니다."
    ),

    // case
    CASE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 사건입니다."
    ),

    ASSIGNEE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "사건 담당자 정보가 없습니다."
    ),

    CASE_STATUS_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "status 값이 비어있습니다."
    ),

    INVALID_CASE_STATUS(
            HttpStatus.BAD_REQUEST,
            "status 값이 올바르지 않습니다."
    ),

    INVALID_CASE_TYPE(
            HttpStatus.BAD_REQUEST,
            "type 값이 올바르지 않습니다."
    ),

    INVALID_ASSIGNEE_ACCESS(
            HttpStatus.FORBIDDEN,
            "사건 담당자만 접근할 수 있습니다."
    ),

    INVALID_CASE_ASSIGNEE_PERMISSION(
            HttpStatus.FORBIDDEN,
            "담당자만 수정 및 삭제가 가능합니다."
    ),

    INVALID_CASE_STATUS_EDIT(
            HttpStatus.BAD_REQUEST,
            "분석 이전 상태의 사건만 수정할 수 있습니다."
    ),

    INVALID_CASE_STATUS_DELETE(
            HttpStatus.BAD_REQUEST,
            "분석 이전 상태의 사건만 삭제할 수 있습니다."
    ),

    // criteria
    CRITERIA_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 법리 판단 기준입니다."
    ),

    CRITERIA_EVALUATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "법리 판단 기준에 대한 평가 정보가 존재하지 않습니다."
    ),

    INVALID_EVALUATION_STATUS(
            HttpStatus.BAD_REQUEST,
            "알 수 없는 status입니다."
    ),

    EVALUATION_STATUS_NOT_INCLUDED(
            HttpStatus.BAD_REQUEST,
            "잘못된 Payload 형식입니다."
    ),

    // investigation file
    INVALID_FILE_TYPE(
            HttpStatus.BAD_REQUEST,
            "자료 유형(type)은 필수 입력값입니다."
    ),

    FILE_NOT_PROVIDED(
            HttpStatus.BAD_REQUEST,
            "파일이 첨부되지 않았습니다."
    ),

    FILE_SIZE_EXCEEDED(
            HttpStatus.BAD_REQUEST,
            "업로드 가능한 파일 크기를 초과했습니다."
    ),

    FILE_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "이미 등록된 파일입니다."
    ),

    FILE_UPLOAD_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "파일 업로드 중 오류가 발생했습니다."
    ),

    // transcript
    TRANSCRIPT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 전사입니다."
    ),

    INVALID_TRANSCRIPT_TYPE(
            HttpStatus.BAD_REQUEST,
            "To AI Server: 잘못된 TRANSCRIPT 분석 결과 형식입니다."
    ),

    // key-statement
    INVALID_KEY_STATEMENT_TYPE(
            HttpStatus.BAD_REQUEST,
            "To AI Server: 잘못된 Key-Statement Payload 형태입니다."
    ),

    KEY_STATEMENT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "id에 해당하는 KeyStatement Entity를 찾을 수 없습니다."
    ),

    // contradiction
    CONTRADICTION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 모순입니다."
    ),

    INVALID_CONTRADICTION_TYPE(
            HttpStatus.BAD_REQUEST,
            "To AI Server: 잘못된 Contradiction Payload 형태입니다."
    ),

    INVALID_CONTRADICTION_STATUS_TRANSITION(
            HttpStatus.CONFLICT,
            "현재 상태에서는 해당 상태로 변경할 수 없습니다."
    ),

    // question
    INVALID_QUESTION_TYPE(
            HttpStatus.BAD_REQUEST,
            "To AI Server: 잘못된 Question Payload 형태입니다."
    ),

    // video-event
    INVALID_VIDEO_EVENT_TYPE(
            HttpStatus.BAD_REQUEST,
            "To AI Server: 잘못된 VideoEvent Payload 형태입니다."
    ),

    // chat-log
    CHAT_LOG_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 Chat Log입니다."
    ),

    EMPTY_CHAT_LOG(
            HttpStatus.BAD_REQUEST,
            "To AI Server: 챗봇 응답이 비어 있습니다."
    ),

    // analysis
    ANALYSIS_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 분석입니다."
    ),

    ANALYSIS_NOT_COMPLETED(
            HttpStatus.CONFLICT,
            "분석이 완료되지 않았습니다."
    ),

    NOT_AUDIO_ANALYSIS(
            HttpStatus.BAD_REQUEST,
            "음성 자료에 대한 분석 결과만 전사 조회가 가능합니다."
    ),

    INVALID_STAGE_TYPE(
            HttpStatus.BAD_REQUEST,
            "To AI Server: 지정되지 않은 응답 Stage 타입입니다."
    ),

    JSON_PARSE_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Analysis 정보 업데이트 도중 Json 파싱에 실패했습니다."
    ),

    ANALYSIS_NOT_IN_CASE(
            HttpStatus.NOT_FOUND,
            "사건과 관련되지 않은 분석 파일이거나, 분석 파일과 연관된 사건 ID가 존재하지 않습니다."
    ),

    FINAL_ANALYSIS_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 최종 통합분석입니다."
    ),

    RECOMMENDED_DECISION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "recommended_decision이 비어있습니다."
    ),

    INVALID_RECOMMENDED_DECISION(
            HttpStatus.BAD_REQUEST,
            "recommended_decision 값이 올바르지 않습니다."
    ),

    // report
    INVALID_ACTIVITY_LOG_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "log_id 선택은 필수입니다."
    ),

    ACTIVITY_LOG_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 log_id가 포함되어 있습니다."
    ),

    REPORT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "report_id에 해당하는 보고서가 존재하지 않습니다."
    ),

    INVALID_ACTIVITY_LOG_SELECTED(
            HttpStatus.BAD_REQUEST,
            "다른 사건의 activity_log는 선택할 수 없습니다."
    ),

    INVALID_DECISION_TYPE(
            HttpStatus.BAD_REQUEST,
            "decision_type 값이 올바르지 않습니다."
    ),

    INVALID_AI_CONTENT_SERIALIZATION(
            HttpStatus.BAD_REQUEST,
            "AI 결과보고 content 직렬화 실패"
    ),

    INVALID_REPORT_TYPE(
            HttpStatus.BAD_REQUEST,
            "type 값이 올바르지 않습니다."
    ),

    UNEXPECTED_REPORT_TYPE(
            HttpStatus.BAD_REQUEST,
            "보고서 type이 일치하지 않습니다."
    ),

    INVALID_REPORT_TYPE_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "report_type은 필수입니다."
    ),

    INVALID_REPORT_ID(
            HttpStatus.BAD_REQUEST,
            "case_id와 report_id가 일치하지 않습니다."
    ),

    INVALID_REPORT_EDIT(
            HttpStatus.BAD_REQUEST,
            "이미 작성 완료된 보고서는 수정할 수 없습니다."
    ),

    REPORT_ALREADY_EXISTS(
            HttpStatus.CONFLICT,
            "이미 작성 완료된 보고서입니다."
    ),

    NOT_DRAFT_REPORT(
            HttpStatus.BAD_REQUEST,
            "임시저장(DRAFT) 문서가 아닙니다."
    ),

    INVALID_DRAFT_DOWNLOAD(
            HttpStatus.BAD_REQUEST,
            "임시저장 보고서는 다운로드할 수 없습니다."
    ),

    INVALID_FINAL_ANALYSIS_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "수사결과보고/수사결정서는 final_id가 필요합니다."
    ),

    // s3
    S3_UPLOAD_FAILED(
            HttpStatus.SERVICE_UNAVAILABLE,
            "S3 업로드에 실패했습니다."
    ),

    INVALID_S3_URI(
            HttpStatus.BAD_REQUEST,
            "유효하지 않은 S3 URI입니다."
    );

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
