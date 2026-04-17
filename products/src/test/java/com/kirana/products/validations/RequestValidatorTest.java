package com.kirana.products.validations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.kirana.products.config.PaginationProperties;
import com.kirana.products.exception.InvalidRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestValidatorTest {

  private RequestValidator requestValidator;

  @BeforeEach
  void setUp() {
    PaginationProperties paginationProperties = new PaginationProperties();
    paginationProperties.setMaxSize(50);
    requestValidator = new RequestValidator(paginationProperties);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 25, 50})
  void validateLimit_shouldPass_whenSizeWithinRange(int size) {
    assertDoesNotThrow(() -> requestValidator.validateLimit(size));
  }

  @ParameterizedTest
  @ValueSource(ints = {0, -1, 51, 100})
  void validateLimit_shouldThrow_whenSizeOutOfRange(int size) {
    InvalidRequestException exception =
        assertThrows(InvalidRequestException.class, () -> requestValidator.validateLimit(size));

    assertEquals("Size must be between 1 and 50", exception.getMessage());
  }
}
