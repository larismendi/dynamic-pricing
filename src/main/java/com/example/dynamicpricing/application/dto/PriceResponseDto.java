package com.example.dynamicpricing.application.dto;

public record PriceResponseDto(
        int productId,
        int brandId,
        int priceList,
        String startDate,
        String endDate,
        double price,
        String currency
) {
}
