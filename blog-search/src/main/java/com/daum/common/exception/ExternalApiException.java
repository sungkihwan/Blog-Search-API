package com.daum.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class ExternalApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
        if (cause instanceof WebClientResponseException) {
            this.httpStatus = ((WebClientResponseException) cause).getStatusCode();
        } else {
            this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

