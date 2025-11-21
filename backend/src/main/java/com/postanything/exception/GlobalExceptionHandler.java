package com.postanything.exception;

import com.postanything.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), null);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex, HttpServletRequest req) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI(), null);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    List<ApiError.FieldError> details = ex.getBindingResult().getFieldErrors().stream()
        .map(f -> new ApiError.FieldError(f.getField(), f.getDefaultMessage()))
        .toList();
    return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), details);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
    List<ApiError.FieldError> details = ex.getConstraintViolations().stream()
        .map(v -> new ApiError.FieldError(v.getPropertyPath().toString(), v.getMessage()))
        .toList();
    return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), details);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI(), null);
  }

  private ResponseEntity<ApiError> build(HttpStatus status, String message, String path, List<ApiError.FieldError> details) {
    ApiError body = new ApiError(status.value(), status.getReasonPhrase(), message, details, path, Instant.now());
    return ResponseEntity.status(status).body(body);
  }
}
