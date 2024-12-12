package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.infrastructure.controller.request.PriceRequest;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceRequestMapperImplTest {

    private static final int BRAND_ID = 5;
    private static final int PRODUCT_ID = 101;
    private static final ZonedDateTime APPLICATION_DATE = ZonedDateTime.of(2023, 12, 1, 14, 0, 0, 0, ZoneId.of("UTC"));

    private final PriceRequestMapperImpl priceRequestMapper = new PriceRequestMapperImpl();

    @Test
    void givenValidPriceRequest_whenToDto_thenAllFieldsAreMappedCorrectly() {
        final PriceRequest priceRequest = PriceRequest.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .applicationDate(APPLICATION_DATE)
                .build();

        final PriceDto result = priceRequestMapper.toDto(priceRequest);

        assertNotNull(result, "The result should not be null");
        assertEquals(PRODUCT_ID, result.productId(), "Product ID should match");
        assertEquals(BRAND_ID, result.brandId(), "Brand ID should match");
        assertEquals(APPLICATION_DATE, result.applicationDate(), "Application date should match");
    }

    @Test
    void givenPriceRequestWithMissingFields_whenToDto_thenDefaultValuesAreUsed() {
        final PriceRequest priceRequest = PriceRequest.builder()
                .productId(PRODUCT_ID)
                .build();

        final PriceDto result = priceRequestMapper.toDto(priceRequest);

        assertNotNull(result, "The result should not be null");
        assertEquals(PRODUCT_ID, result.productId(), "Product ID should match");
        assertEquals(0, result.brandId(), "Default Brand ID should be 0");
        assertNull(result.applicationDate(), "Default Application Date should be null");
    }
}
