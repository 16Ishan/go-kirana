package com.kirana.products.validations;

import com.kirana.products.exception.InvalidRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

  @Value("${app.pagination.max-size}")
  private int maxPageSize;

  public void validateLimit(int size) {
    if (size > maxPageSize) {
      throw new InvalidRequestException("Size cannot exceed: " + maxPageSize);
    }
  }
}
