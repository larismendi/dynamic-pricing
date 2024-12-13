package com.example.dynamicpricing.infrastructure.controller.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


class PriceResponseTests {

    private static final int BRAND_ID = 101;
    private static final int PRODUCT_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 6, 14, 23, 59, 59);
    private static final int PRICE_LIST = 1001;
    private static final String CURRENCY = "USD";
    private static final double PRICE = 99.99;
    private static final double NEGATIVE_PRICE = -99.99;
    private static final String EMPTY_START_DATE = "";
    private static final String EMPTY_END_DATE = "";
    private static final String NULL_START_DATE = null;
    private static final String NULL_END_DATE = null;

    @Test
    void givenValidPriceResponse_whenChecked_thenNoErrors() {
        final PriceResponse priceResponse = PriceResponse.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .startDate(START_DATE.toString())
                .endDate(END_DATE.toString())
                .price(PRICE)
                .currency(CURRENCY)
                .build();

        assertNotNull(priceResponse);
        assertEquals(PRODUCT_ID, priceResponse.getProductId());
        assertEquals(BRAND_ID, priceResponse.getBrandId());
        assertEquals(PRICE_LIST, priceResponse.getPriceList());
        assertEquals(START_DATE.toString(), priceResponse.getStartDate());
        assertEquals(END_DATE.toString(), priceResponse.getEndDate());
        assertEquals(PRICE, priceResponse.getPrice());
        assertEquals(CURRENCY, priceResponse.getCurrency());

        assertTrue(priceResponse.getPrice() > 0);
    }

    @Test
    void givenNullDates_whenChecked_thenValidationFails() {
        final PriceResponse priceResponse = PriceResponse.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .startDate(NULL_START_DATE)
                .endDate(NULL_END_DATE)
                .price(PRICE)
                .currency(CURRENCY)
                .build();

        assertNotNull(priceResponse);

        assertNull(priceResponse.getStartDate());
        assertNull(priceResponse.getEndDate());
    }

    @Test
    void givenNegativePrice_whenChecked_thenPriceShouldBeInvalid() {
        final PriceResponse priceResponse = PriceResponse.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .startDate(START_DATE.toString())
                .endDate(END_DATE.toString())
                .price(NEGATIVE_PRICE)
                .currency(CURRENCY)
                .build();

        assertNotNull(priceResponse);

        assertTrue(priceResponse.getPrice() < 0);
    }

    @Test
    void givenEmptyFields_whenChecked_thenDefaultValues() {
        final PriceResponse priceResponse = PriceResponse.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .startDate(EMPTY_START_DATE)
                .endDate(EMPTY_END_DATE)
                .price(PRICE)
                .currency(CURRENCY)
                .build();

        assertNotNull(priceResponse);

        assertEquals(EMPTY_START_DATE, priceResponse.getStartDate());
        assertEquals(EMPTY_END_DATE, priceResponse.getEndDate());
    }
}
