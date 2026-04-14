package com.kirana.products.validations;

import com.kirana.products.config.PaginationProperties;
import com.kirana.products.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestValidator {

  private final PaginationProperties paginationProperties;

  public void validateLimit(int size) {
    int maxPageSize = paginationProperties.getMaxSize();
    if (size < 1 || size > maxPageSize) {
      throw new InvalidRequestException("Size must be between 1 and " + maxPageSize);
    }
  }
}
