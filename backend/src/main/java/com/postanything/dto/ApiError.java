package com.postanything.dto;

import java.time.Instant;
import java.util.List;

public record ApiError(
    int status,
    String error,
    String message,
    List<FieldError> details,
    String path,
    Instant timestamp
) {
  public record FieldError(String field, String message) {}
}
