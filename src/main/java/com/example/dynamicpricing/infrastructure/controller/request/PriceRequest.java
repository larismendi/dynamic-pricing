package com.example.dynamicpricing.infrastructure.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Data
@Builder
public class PriceRequest {
    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Integer productId;

    @NotNull(message = "Brand ID is required")
    @Positive(message = "Brand ID must be positive")
    private Integer brandId;

    @NotNull(message = "Application date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime applicationDate;
}
