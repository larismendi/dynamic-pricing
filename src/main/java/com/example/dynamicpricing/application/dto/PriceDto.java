package com.example.dynamicpricing.application.dto;

import java.time.LocalDateTime;

public record PriceDto(
        int productId,
        int brandId,
        LocalDateTime applicationDate
) {}
