package com.example.dynamicpricing.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 100;
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(99.99);
    private static final String CURRENCY = "EUR";

    private static Price price;

    void getPriceBuilder(LocalDateTime startDate, LocalDateTime endDate) {
        price = Price.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .startDate(startDate)
                .endDate(endDate)
                .priceList(PRICE_LIST)
                .priority(PRIORITY)
                .price(PRICE)
                .currency(CURRENCY)
                .build();
    }


    @Test
    void testIsApplicableWhenDateIsWithinRange() {
        LocalDateTime startDate = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 1, 23, 59);
        LocalDateTime applicationDate = LocalDateTime.of(2023, 11, 15, 12, 0);

        getPriceBuilder(startDate, endDate);

        assertTrue(price.isApplicable(applicationDate));
    }

    @Test
    void testIsApplicableWhenDateIsEqualToStartDate() {
        LocalDateTime startDate = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 1, 23, 59);
        LocalDateTime applicationDate = LocalDateTime.of(2023, 11, 1, 0, 0);

        getPriceBuilder(startDate, endDate);

        assertTrue(price.isApplicable(applicationDate));
    }

    @Test
    void testIsApplicableWhenDateIsEqualToEndDate() {
        LocalDateTime startDate = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 1, 23, 59);
        LocalDateTime applicationDate = LocalDateTime.of(2023, 12, 1, 23, 59);

        getPriceBuilder(startDate, endDate);

        assertTrue(price.isApplicable(applicationDate));
    }

    @Test
    void testIsApplicableWhenDateIsBeforeStartDate() {
        LocalDateTime startDate = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 1, 23, 59);
        LocalDateTime applicationDate = LocalDateTime.of(2023, 10, 31, 23, 59);

        getPriceBuilder(startDate, endDate);

        assertFalse(price.isApplicable(applicationDate));
    }

    @Test
    void testIsApplicableWhenDateIsAfterEndDate() {
        LocalDateTime startDate = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 1, 23, 59);
        LocalDateTime applicationDate = LocalDateTime.of(2023, 12, 2, 0, 0);

        getPriceBuilder(startDate, endDate);

        assertFalse(price.isApplicable(applicationDate));
    }

    @Test
    void testPriceBuilderCreatesCorrectObject() {
        LocalDateTime startDate = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 1, 23, 59);

        getPriceBuilder(startDate, endDate);

        assertEquals(BRAND_ID, price.getBrandId());
        assertEquals(PRODUCT_ID, price.getProductId());
        assertEquals(startDate, price.getStartDate());
        assertEquals(endDate, price.getEndDate());
        assertEquals(PRICE_LIST, price.getPriceList());
        assertEquals(PRIORITY, price.getPriority());
        assertEquals(PRICE, price.getPrice());
        assertEquals(CURRENCY, price.getCurrency());
    }
}

