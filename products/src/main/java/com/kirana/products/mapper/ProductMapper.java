package com.kirana.products.mapper;

import com.kirana.products.dto.ProductDto;
import com.kirana.products.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
  public ProductDto toDto(Product product) {
    return new ProductDto(
        product.getId(),
        product.getName(),
        product.getDescription(),
        product.getCategory() != null ? product.getCategory().getName() : null,
        product.getPrice(),
        product.getImageUrl(),
        product.isAvailable(),
        product.getCreatedAt(),
        product.getUpdatedAt());
  }
}
