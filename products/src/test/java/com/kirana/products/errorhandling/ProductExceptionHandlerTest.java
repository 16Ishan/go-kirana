package com.kirana.products.errorhandling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kirana.products.exception.InvalidRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class ProductExceptionHandlerTest {

  private static class RequestPayload {
    @SuppressWarnings("unused")
    private String size;
  }

  private static class MethodParameterSource {
    @SuppressWarnings("unused")
    void handle(Integer size) {}
  }

  private ProductExceptionHandler exceptionHandler;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    exceptionHandler = new ProductExceptionHandler();
    request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/v1/api/product/products");
  }

  @Test
  void handleBadRequestExceptions_shouldHandleTypeMismatch() throws NoSuchMethodException {
    MethodParameter methodParameter =
        new MethodParameter(
            MethodParameterSource.class.getDeclaredMethod("handle", Integer.class), 0);
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException(
            "abc", Integer.class, "size", methodParameter, null);

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleBadRequestExceptions(ex, request);
    ErrorResponse errorResponse = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(errorResponse);
    assertEquals("INVALID_REQUEST", errorResponse.code());
    assertEquals("Invalid value 'abc' for parameter 'size'", errorResponse.message());
  }

  @Test
  void handleBadRequestExceptions_shouldHandleMissingParameter() {
    MissingServletRequestParameterException ex =
        mock(MissingServletRequestParameterException.class);
    when(ex.getParameterName()).thenReturn("size");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleBadRequestExceptions(ex, request);
    ErrorResponse errorResponse = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(errorResponse);
    assertEquals("Missing required parameter: size", errorResponse.message());
  }

  @Test
  void handleBadRequestExceptions_shouldHandleBindExceptionFieldMessage() {
    RequestPayload payload = new RequestPayload();
    DirectFieldBindingResult bindingResult = new DirectFieldBindingResult(payload, "request");
    bindingResult.rejectValue("size", "invalid", "Size must be a number");
    BindException ex = new BindException(bindingResult);

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleBadRequestExceptions(ex, request);
    ErrorResponse errorResponse = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(errorResponse);
    assertEquals("Size must be a number", errorResponse.message());
  }

  @Test
  void handleBadRequestExceptions_shouldHandleConstraintViolationMessage() {
    @SuppressWarnings("unchecked")
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    when(violation.getMessage()).thenReturn("Category must not be blank");

    ConstraintViolationException ex =
        new ConstraintViolationException("invalid", Set.of(violation));

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleBadRequestExceptions(ex, request);
    ErrorResponse errorResponse = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(errorResponse);
    assertEquals("Category must not be blank", errorResponse.message());
  }

  @Test
  void handleBadRequestExceptions_shouldHandleMalformedBody() {
    HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleBadRequestExceptions(ex, request);
    ErrorResponse errorResponse = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(errorResponse);
    assertEquals("Malformed request body", errorResponse.message());
  }

  @Test
  void handleBadRequestExceptions_shouldFallbackToInvalidRequestMessage() {
    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleBadRequestExceptions(new IllegalArgumentException("bad"), request);
    ErrorResponse errorResponse = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(errorResponse);
    assertEquals("Invalid request", errorResponse.message());
  }

  @Test
  void handleInvalidRequestException_shouldReturnBadRequestResponse() {
    InvalidRequestException ex = new InvalidRequestException("Size must be between 1 and 100");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleInvalidRequestException(ex, request);
    ErrorResponse errorResponse = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(errorResponse);
    assertEquals("INVALID_REQUEST", errorResponse.code());
    assertEquals("Size must be between 1 and 100", errorResponse.message());
    assertEquals("/v1/api/product/products", errorResponse.path());
    assertNotNull(errorResponse.timestamp());
  }

  @Test
  void handleGenericException_shouldReturnInternalServerErrorResponse() {
    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleGenericException(new RuntimeException("boom"), request);
    ErrorResponse errorResponse = response.getBody();

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(errorResponse);
    assertEquals("UNEXPECTED_ERROR", errorResponse.code());
    assertEquals("An unexpected error occurred. Please contact support.", errorResponse.message());
    assertEquals("/v1/api/product/products", errorResponse.path());
    assertNotNull(errorResponse.timestamp());
  }
}
