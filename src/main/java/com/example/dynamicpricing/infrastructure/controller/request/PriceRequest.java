package com.example.dynamicpricing.infrastructure.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class PriceRequest {
    @Positive(message = "Product ID must be positive")
    private int productId;

    @Positive(message = "Brand ID must be positive")
    private int brandId;

    @NotNull(message = "Application date must not be null")
    private ZonedDateTime applicationDate;
}
