package com.kirana.products.mapper;

import com.kirana.products.dto.ProductDto;
import com.kirana.products.repository.projection.ProductProjection;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
  public ProductDto toDto(ProductProjection projection) {
    return new ProductDto(
        projection.getId(),
        projection.getName(),
        projection.getDescription(),
        projection.getCategoryName(),
        projection.getPrice(),
        projection.getImageUrl(),
        Boolean.TRUE.equals(projection.getIsAvailable()),
        projection.getCreatedAt() == null ? null : projection.getCreatedAt(),
        projection.getUpdatedAt() == null ? null : projection.getUpdatedAt());
  }
}
