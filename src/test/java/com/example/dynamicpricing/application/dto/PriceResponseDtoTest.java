package com.example.dynamicpricing.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PriceResponseDtoTest {

    private static final int PRODUCT_ID = 35455;
    private static final int BRAND_ID = 1;
    private static final int PRICE_LIST = 1;
    private static final String START_DATE = "2020-06-14T00:00:00Z";
    private static final String END_DATE = "2020-12-31T23:59:59Z";
    private static final double PRICE = 35.5;
    private static final String CURRENCY = "EUR";
    private static final String NULL_START_DATE = null;
    private static final String NULL_END_DATE = null;

    @Test
    void givenValidData_whenCreatePriceResponseDto_thenFieldsAreSetCorrectly() {
        final PriceResponseDto priceResponseDto = new PriceResponseDto(
                PRODUCT_ID, BRAND_ID, PRICE_LIST, START_DATE, END_DATE, PRICE, CURRENCY);

        assertEquals(PRODUCT_ID, priceResponseDto.productId());
        assertEquals(BRAND_ID, priceResponseDto.brandId());
        assertEquals(PRICE_LIST, priceResponseDto.priceList());
        assertEquals(START_DATE, priceResponseDto.startDate());
        assertEquals(END_DATE, priceResponseDto.endDate());
        assertEquals(PRICE, priceResponseDto.price());
        assertEquals(CURRENCY, priceResponseDto.currency());
    }

    @Test
    void givenNullData_whenCreatePriceResponseDto_thenFieldsAreSetCorrectly() {
        final PriceResponseDto priceResponseDto = new PriceResponseDto(
                PRODUCT_ID, BRAND_ID, PRICE_LIST, NULL_START_DATE, NULL_END_DATE, PRICE, CURRENCY);

        assertNull(priceResponseDto.startDate());
        assertNull(priceResponseDto.endDate());
    }
}
