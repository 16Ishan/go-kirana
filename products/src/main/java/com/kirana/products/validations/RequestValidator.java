package com.kirana.products.validations;

import com.kirana.products.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

  @Value("${app.pagination.max-size}")
  private int maxPageSize;

  public void validateLimit(int size) {
    if (maxPageSize <= 0) {
      throw new InvalidRequestException("Configured max page size must be greater than 0");
    }
    if (size < 1 || size > maxPageSize) {
      throw new InvalidRequestException("Size must be between 1 and " + maxPageSize);
    }
  }
}
