package com.kirana.products.service;

import com.kirana.products.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
  Page<ProductDto> getProducts(String category, String name, Pageable pageable);
}
