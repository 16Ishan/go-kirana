package com.kirana.products.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kirana.products.dto.ProductDto;
import com.kirana.products.exception.InvalidRequestException;
import com.kirana.products.service.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ProductService productService;

  @Test
  void getProducts_shouldReturnPage_whenFiltersProvided() throws Exception {
    ProductDto dto =
        new ProductDto(
            UUID.randomUUID(),
            "Milk",
            "Fresh milk",
            "Dairy",
            new BigDecimal("60.00"),
            "milk.png",
            true,
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now());

    PageRequest pageRequest = PageRequest.of(0, 10);
    when(productService.getProducts(eq("dairy"), eq("milk"), eq(pageRequest)))
        .thenReturn(new PageImpl<>(List.of(dto), pageRequest, 1));

    mockMvc
        .perform(
            get("/v1/api/product/products")
                .param("category", "dairy")
                .param("name", "milk")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Milk"))
        .andExpect(jsonPath("$.content[0].categoryName").value("Dairy"));

    verify(productService).getProducts(eq("dairy"), eq("milk"), eq(pageRequest));
  }

  @Test
  void getProducts_shouldUseNullFilters_whenParamsMissing() throws Exception {
    Pageable defaultPageable = PageRequest.of(0, 20);
    when(productService.getProducts(eq(null), eq(null), eq(defaultPageable)))
        .thenReturn(new PageImpl<>(List.of(), defaultPageable, 0));

    mockMvc
        .perform(get("/v1/api/product/products").param("page", "0").param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(0));

    verify(productService).getProducts(eq(null), eq(null), eq(defaultPageable));
  }

  @Test
  void getProducts_shouldReturnBadRequest_whenServiceThrowsInvalidRequest() throws Exception {
    when(productService.getProducts(eq("dairy"), eq("milk"), eq(PageRequest.of(0, 500))))
        .thenThrow(new InvalidRequestException("Size must be between 1 and 100"));

    mockMvc
        .perform(
            get("/v1/api/product/products")
                .param("category", "dairy")
                .param("name", "milk")
                .param("page", "0")
                .param("size", "500"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.message").value("Size must be between 1 and 100"))
        .andExpect(jsonPath("$.path").value("/v1/api/product/products"));
  }
}
