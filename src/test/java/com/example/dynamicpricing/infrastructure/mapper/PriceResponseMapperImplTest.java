package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PriceResponseMapperImplTest {

    private static final int PRODUCT_ID = 35455;
    private static final int BRAND_ID = 1;
    private static final int PRICE_LIST = 1;
    private static final String START_DATE = "2020-06-14T00:00:00Z";
    private static final String END_DATE = "2020-12-31T23:59:59Z";
    private static final double PRICE = 35.5;
    private static final String CURRENCY = "EUR";
    private static final String NULL_START_DATE = null;
    private static final String NULL_END_DATE = null;

    private PriceResponseMapperImpl priceResponseMapper;

    @BeforeEach
    void setUp() {
        priceResponseMapper = new PriceResponseMapperImpl();
    }

    @Test
    void givenPriceResponseDto_whenMappedToPriceResponse_thenFieldsAreMappedCorrectly() {
        final PriceResponseDto priceResponseDto = new PriceResponseDto(
                PRODUCT_ID, BRAND_ID, PRICE_LIST, START_DATE, END_DATE, PRICE, CURRENCY);

        final PriceResponse priceResponse = priceResponseMapper.toPriceResponse(priceResponseDto);

        assertEquals(PRODUCT_ID, priceResponse.getProductId());
        assertEquals(BRAND_ID, priceResponse.getBrandId());
        assertEquals(PRICE_LIST, priceResponse.getPriceList());
        assertEquals(START_DATE, priceResponse.getStartDate());
        assertEquals(END_DATE, priceResponse.getEndDate());
        assertEquals(PRICE, priceResponse.getPrice());
        assertEquals(CURRENCY, priceResponse.getCurrency());
    }

    @Test
    void givenPriceResponseDtoWithNullValues_whenMappedToPriceResponse_thenFieldsAreMappedCorrectly() {
        final PriceResponseDto priceResponseDto = new PriceResponseDto(
                PRODUCT_ID, BRAND_ID, PRICE_LIST, NULL_START_DATE, NULL_END_DATE, PRICE, CURRENCY);

        final PriceResponse priceResponse = priceResponseMapper.toPriceResponse(priceResponseDto);

        assertEquals(PRODUCT_ID, priceResponse.getProductId());
        assertEquals(BRAND_ID, priceResponse.getBrandId());
        assertEquals(PRICE_LIST, priceResponse.getPriceList());
        assertNull(priceResponse.getStartDate());
        assertNull(priceResponse.getEndDate());
        assertEquals(PRICE, priceResponse.getPrice());
        assertEquals(CURRENCY, priceResponse.getCurrency());
    }
}
