package com.aivle.big_project.ai;

import lombok.Getter;

public class AiClientException extends RuntimeException {
    @Getter
    private final int statusCode;
    @Getter
    private final String responseBody;

    public AiClientException(int statusCode, String responseBody) {
        super("AI call failed. status=" + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

}
