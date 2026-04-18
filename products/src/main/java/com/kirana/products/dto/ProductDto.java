package com.kirana.products.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDto {
  private UUID id;
  private String name;
  private String description;
  private String categoryName;
  private BigDecimal price;
  private String imageUrl;
  private boolean isAvailable;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
