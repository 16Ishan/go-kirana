package com.kirana.products.errorhandling;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Standard error response")
public record ErrorResponse(
    @Schema(example = "2026-04-14T12:30:45") LocalDateTime timestamp,
    @Schema(example = "400") int status,
    @Schema(example = "Bad Request") String error,
    @Schema(example = "INVALID_REQUEST") String code,
    @Schema(example = "Size cannot exceed: 100") String message,
    @Schema(example = "/v1/api/product/products") String path) {}
