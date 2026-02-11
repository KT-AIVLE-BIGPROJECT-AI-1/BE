package com.aivle.big_project.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // case
    CASE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 사건입니다."
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

    INVALID_CRITERIA_EVALUATION_REQUEST(
            HttpStatus.BAD_REQUEST,
            "평가 상태와 평가 사유는 필수 입력값입니다."
    ),

    CRITERIA_DELETE_REASON_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "판단 기준 삭제 사유는 필수 입력값입니다."
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

    // key-statement
    INVALID_KEY_STATEMENT_TYPE(
            HttpStatus.BAD_REQUEST,
            "To AI Server: 잘못된 Key-Statement Payload 형태입니다."
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
    )
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
