package com.daum.common.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        List<String> errors = getErrorsFromFieldErrors(e.getFieldErrors());
        ErrorResponse response =
                createErrorResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), "Bad Request", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handleWebExchangeBindException(WebExchangeBindException e, ServerWebExchange exchange) {
        List<String> errors = getErrorsFromFieldErrors(e.getFieldErrors());
        ErrorResponse response =
                createErrorResponse(HttpStatus.BAD_REQUEST, exchange.getRequest().getPath().toString(), "Bad Request", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(ExternalApiException e, HttpServletRequest request) {
        ErrorResponse response =
                createErrorResponse(e.getHttpStatus(), request.getRequestURI(), "외부 API 호출 에러", Collections.singletonList(e.getMessage()));

        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(KeywordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleKeywordNotFoundException(KeywordNotFoundException e, HttpServletRequest request) {
        ErrorResponse response =
                createErrorResponse(HttpStatus.NOT_FOUND, request.getRequestURI(), "키워드를 찾을 수 없습니다.", Collections.singletonList(e.getMessage()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(KeywordUpdateException.class)
    public ResponseEntity<ErrorResponse> handleKeywordUpdateException(KeywordUpdateException e, HttpServletRequest request) {
        ErrorResponse response =
                createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), "키워드 업데이트 중 오류가 발생했습니다.", Collections.singletonList(e.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    private List<String> getErrorsFromFieldErrors(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }

    private ErrorResponse createErrorResponse(HttpStatus httpStatus, String requestUri, String error, List<String> errors) {
        return new ErrorResponse(
                httpStatus.value(),
                new Date(),
                error,
                requestUri,
                errors);
    }
}

