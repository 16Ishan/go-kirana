package com.kirana.products.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ProductProjection {
  UUID getId();

  String getName();

  String getDescription();

  String getCategoryName();

  BigDecimal getPrice();

  String getImageUrl();

  Boolean getIsAvailable();

  LocalDateTime getCreatedAt();

  LocalDateTime getUpdatedAt();
}
