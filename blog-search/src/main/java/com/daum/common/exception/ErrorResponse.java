package com.daum.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
public class ErrorResponse {
  private final int status;
  private final Date timestamp;
  private final String error;
  private final String path;
  private final List<String> errors;
}