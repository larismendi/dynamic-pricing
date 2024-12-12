package com.example.dynamicpricing.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 100;
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(99.99);
    private static final String CURRENCY = "EUR";
    private static final ZonedDateTime START_DATE = ZonedDateTime.of(2023, 11, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime END_DATE = ZonedDateTime.of(2023, 12, 1, 23, 59, 59, 0, ZoneId.of("UTC"));

    private static Price price;

    void getPriceBuilder() {
        price = Price.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .priority(PRIORITY)
                .price(PRICE)
                .currency(CURRENCY)
                .build();
    }


    @Test
    void testIsApplicableWhenDateIsWithinRange() {
        final ZonedDateTime applicationDate = ZonedDateTime.of(2023, 11, 15, 12, 0, 0, 0, ZoneId.of("UTC"));

        getPriceBuilder();

        assertTrue(price.isApplicable(applicationDate));
    }

    @Test
    void testIsApplicableWhenDateIsEqualToStartDate() {
        final ZonedDateTime applicationDate = ZonedDateTime.of(2023, 11, 1, 0, 0, 0, 0, ZoneId.of("UTC"));

        getPriceBuilder();

        assertTrue(price.isApplicable(applicationDate));
    }

    @Test
    void testIsApplicableWhenDateIsEqualToEndDate() {
        final ZonedDateTime applicationDate = ZonedDateTime.of(2023, 12, 1, 23, 59, 59, 0, ZoneId.of("UTC"));

        getPriceBuilder();

        assertTrue(price.isApplicable(applicationDate));
    }

    @Test
    void testIsApplicableWhenDateIsBeforeStartDate() {
        final ZonedDateTime applicationDate = ZonedDateTime.of(2023, 10, 31, 23, 59, 59, 0, ZoneId.of("UTC"));

        getPriceBuilder();

        assertFalse(price.isApplicable(applicationDate));
    }

    @Test
    void testIsApplicableWhenDateIsAfterEndDate() {
        final ZonedDateTime applicationDate = ZonedDateTime.of(2023, 12, 2, 0, 0, 0, 0, ZoneId.of("UTC"));

        getPriceBuilder();

        assertFalse(price.isApplicable(applicationDate));
    }

    @Test
    void testPriceBuilderCreatesCorrectObject() {
        getPriceBuilder();

        assertEquals(BRAND_ID, price.getBrandId());
        assertEquals(PRODUCT_ID, price.getProductId());
        assertEquals(START_DATE, price.getStartDate());
        assertEquals(END_DATE, price.getEndDate());
        assertEquals(PRICE_LIST, price.getPriceList());
        assertEquals(PRIORITY, price.getPriority());
        assertEquals(PRICE, price.getPrice());
        assertEquals(CURRENCY, price.getCurrency());
    }
}

