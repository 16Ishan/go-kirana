package com.kirana.products.errorhandling;

import com.kirana.products.exception.InvalidRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class ProductExceptionHandler {

  @ExceptionHandler({
    MethodArgumentTypeMismatchException.class,
    BindException.class,
    MissingServletRequestParameterException.class,
    HttpMessageNotReadableException.class,
    ConstraintViolationException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequestExceptions(
      Exception ex, HttpServletRequest request) {

    String message = resolveBadRequestMessage(ex);
    log.warn("Client request error: {}", message, ex);

    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message, request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex, HttpServletRequest request) {

    log.error("Unhandled exception occurred", ex);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "UNEXPECTED_ERROR",
        "An unexpected error occurred. Please contact support.",
        request.getRequestURI());
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRequestException(
      InvalidRequestException ex, HttpServletRequest request) {

    log.warn("Invalid request: {}", ex.getMessage(), ex);
    return buildErrorResponse(
        HttpStatus.BAD_REQUEST, "INVALID_REQUEST", ex.getMessage(), request.getRequestURI());
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus status, String code, String message, String path) {
    ErrorResponse errorResponse =
        new ErrorResponse(
            LocalDateTime.now(), status.value(), status.getReasonPhrase(), code, message, path);

    return ResponseEntity.status(status).body(errorResponse);
  }

  private String resolveBadRequestMessage(Exception ex) {
    if (ex instanceof MethodArgumentTypeMismatchException mismatchException) {
      return String.format(
          "Invalid value '%s' for parameter '%s'",
          mismatchException.getValue(), mismatchException.getName());
    }

    if (ex instanceof MissingServletRequestParameterException missingParameterException) {
      return "Missing required parameter: " + missingParameterException.getParameterName();
    }

    if (ex instanceof BindException bindException
        && bindException.getBindingResult().hasFieldErrors()) {
      return bindException.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();
    }

    if (ex instanceof ConstraintViolationException constraintViolationException
        && !constraintViolationException.getConstraintViolations().isEmpty()) {
      return constraintViolationException.getConstraintViolations().iterator().next().getMessage();
    }

    if (ex instanceof HttpMessageNotReadableException) {
      return "Malformed request body";
    }

    return "Invalid request";
  }
}
