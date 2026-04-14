package com.kirana.products.service.impl;

import com.kirana.products.dto.ProductDto;
import com.kirana.products.mapper.ProductMapper;
import com.kirana.products.repository.ProductRepository;
import com.kirana.products.service.ProductService;
import com.kirana.products.validations.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final RequestValidator requestValidator;
  private final ProductMapper productMapper;

  @Override
  public Page<ProductDto> getProducts(String category, String name, Pageable pageable) {

    requestValidator.validateLimit(pageable.getPageSize());
    if (StringUtils.isBlank(category)) category = null;
    if (StringUtils.isBlank(name)) name = null;

    return productRepository
        .findByCategoryAndName(category, name, pageable)
        .map(productMapper::toDto);
  }
}
