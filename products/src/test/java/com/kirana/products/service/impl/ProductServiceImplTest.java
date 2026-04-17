package com.kirana.products.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kirana.products.dto.ProductDto;
import com.kirana.products.entity.Product;
import com.kirana.products.mapper.ProductMapper;
import com.kirana.products.repository.ProductRepository;
import com.kirana.products.validations.RequestValidator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock private ProductRepository productRepository;
  @Mock private RequestValidator requestValidator;
  @Mock private ProductMapper productMapper;

  @InjectMocks private ProductServiceImpl productService;

  @Captor private ArgumentCaptor<Specification<Product>> specificationCaptor;

  private Pageable pageable;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(0, 20);
  }

  @Test
  void getProducts_shouldValidateLimitAndMapResult_whenCategoryBlank() {
    Product product = new Product();
    product.setId(UUID.randomUUID());

    ProductDto dto =
        new ProductDto(
            product.getId(),
            "Milk",
            "Fresh",
            "Dairy",
            new BigDecimal("59.99"),
            "img.png",
            true,
            LocalDateTime.now().minusHours(2),
            LocalDateTime.now());

    when(productRepository.findAll(
            org.mockito.ArgumentMatchers.<Specification<Product>>any(), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(product), pageable, 1));
    when(productMapper.toDto(product)).thenReturn(dto);

    Page<ProductDto> result = productService.getProducts("  ", "milk", pageable);

    verify(requestValidator).validateLimit(20);
    verify(productRepository).findAll(specificationCaptor.capture(), eq(pageable));
    verify(productMapper).toDto(product);

    assertEquals(1, result.getTotalElements());
    assertSame(dto, result.getContent().getFirst());
  }

  @Test
  void getProducts_shouldValidateLimitAndMapResult_whenCategoryProvided() {
    Product product = new Product();
    product.setId(UUID.randomUUID());

    ProductDto dto =
        new ProductDto(
            product.getId(),
            "Paneer",
            "Soft",
            "Dairy",
            new BigDecimal("95.00"),
            "paneer.png",
            true,
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now());

    when(productRepository.findAll(
            org.mockito.ArgumentMatchers.<Specification<Product>>any(), eq(pageable)))
        .thenReturn(new PageImpl<>(List.of(product), pageable, 1));
    when(productMapper.toDto(product)).thenReturn(dto);

    Page<ProductDto> result = productService.getProducts("dairy", "paneer", pageable);

    verify(requestValidator).validateLimit(20);
    verify(productRepository).findAll(specificationCaptor.capture(), eq(pageable));
    verify(productMapper).toDto(product);

    assertEquals(1, result.getTotalElements());
    assertEquals("Paneer", result.getContent().getFirst().getName());
  }
}
