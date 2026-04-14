package com.kirana.products.controller;

import com.kirana.products.dto.ProductDto;
import com.kirana.products.errorhandling.ErrorResponse;
import com.kirana.products.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/product")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @Operation(
      summary = "Get products",
      description =
          "Fetches a paginated list of products. Results can be filtered by category and product name.")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Products fetched successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request parameters",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/products")
  public ResponseEntity<Page<ProductDto>> getProducts(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String name,
      @ParameterObject Pageable pageable) {

    return ResponseEntity.ok(productService.getProducts(category, name, pageable));
  }
}
