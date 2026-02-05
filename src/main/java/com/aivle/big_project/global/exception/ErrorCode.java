package com.aivle.big_project.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    CRITERIA_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 법리 판단 기준입니다."
    ),

    CRITERIA_ALREADY_ANALYZED(
            HttpStatus.CONFLICT,
            "분석이 진행 중이거나 완료된 법리 판단 기준은 삭제할 수 없습니다."
    ),

    CRITERIA_EVALUATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "법리 판단 기준에 대한 평가 정보가 존재하지 않습니다."
    ),

    INVALID_CRITERIA_EVALUATION_REQUEST(
            HttpStatus.BAD_REQUEST,
            "평가 상태와 평가 사유는 필수 입력값입니다."
    );


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
