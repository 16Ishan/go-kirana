package com.kirana.products.config;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.pagination")
@Getter
@Setter
public class PaginationProperties {

  @Min(value = 1, message = "Max page size must be greater than 0")
  private int maxSize;
}
