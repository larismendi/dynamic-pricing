package com.example.dynamicpricing.application.dto;

import java.time.ZonedDateTime;

public record PriceDto(
        int productId,
        int brandId,
        ZonedDateTime applicationDate
) {
}
