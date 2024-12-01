package com.example.dynamicpricing.application.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PriceDtoTest {

    private static final int BRAND_ID = 5;
    private static final int PRODUCT_ID = 101;
    private static final int BRAND_ID_2 = 6;
    private static final int PRODUCT_ID_2 = 102;
    private static final int NEGATIVE_PRODUCT_ID = -1;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2023, 12, 1, 14, 0, 0);

    @Test
    void givenValidInputs_whenCreatingPriceDto_thenAllFieldsAreSetCorrectly() {
        PriceDto priceDto = new PriceDto(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertEquals(PRODUCT_ID, priceDto.productId());
        assertEquals(BRAND_ID, priceDto.brandId());
        assertEquals(APPLICATION_DATE, priceDto.applicationDate());
    }

    @Test
    void givenNegativeProductId_whenCreatingPriceDto_thenFieldIsSet() {
        PriceDto priceDto = new PriceDto(NEGATIVE_PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertEquals(NEGATIVE_PRODUCT_ID, priceDto.productId());
    }

    @Test
    void givenTwoIdenticalPriceDto_whenCheckingEquality_thenTheyAreEqual() {
        PriceDto dto1 = new PriceDto(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
        PriceDto dto2 = new PriceDto(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void givenTwoDifferentPriceDto_whenCheckingEquality_thenTheyAreNotEqual() {
        PriceDto dto1 = new PriceDto(PRODUCT_ID, BRAND_ID, LocalDateTime.of(2020, 6, 14, 16, 0, 0));
        PriceDto dto2 = new PriceDto(PRODUCT_ID_2, BRAND_ID_2, LocalDateTime.of(2020, 6, 15, 16, 0, 0));

        assertNotEquals(dto1, dto2);
    }
}

