package com.kirana.products.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.kirana.products.dto.ProductDto;
import com.kirana.products.entity.Category;
import com.kirana.products.entity.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

  private ProductMapper productMapper;

  @BeforeEach
  void setUp() {
    productMapper = new ProductMapper();
  }

  @Test
  void toDto_shouldMapAllFields_whenCategoryPresent() {
    Category category = new Category();
    category.setName("Dairy");

    UUID id = UUID.randomUUID();
    LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    LocalDateTime updatedAt = LocalDateTime.now();

    Product product = new Product();
    product.setId(id);
    product.setName("Milk");
    product.setDescription("Full cream milk");
    product.setCategory(category);
    product.setPrice(new BigDecimal("59.99"));
    product.setImageUrl("https://cdn.example.com/milk.png");
    product.setAvailable(true);
    product.setCreatedAt(createdAt);
    product.setUpdatedAt(updatedAt);

    ProductDto dto = productMapper.toDto(product);

    assertEquals(id, dto.getId());
    assertEquals("Milk", dto.getName());
    assertEquals("Full cream milk", dto.getDescription());
    assertEquals("Dairy", dto.getCategoryName());
    assertEquals(new BigDecimal("59.99"), dto.getPrice());
    assertEquals("https://cdn.example.com/milk.png", dto.getImageUrl());
    assertTrue(dto.isAvailable());
    assertEquals(createdAt, dto.getCreatedAt());
    assertEquals(updatedAt, dto.getUpdatedAt());
  }

  @Test
  void toDto_shouldSetCategoryNameNull_whenCategoryMissing() {
    Product product = new Product();
    product.setId(UUID.randomUUID());
    product.setName("Rice");
    product.setCategory(null);
    product.setAvailable(false);

    ProductDto dto = productMapper.toDto(product);

    assertNull(dto.getCategoryName());
    assertFalse(dto.isAvailable());
    assertEquals("Rice", dto.getName());
  }
}
