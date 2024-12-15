package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.domain.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceUseCaseMapperTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final ZonedDateTime START_DATE = ZonedDateTime.of(2020, 6, 14, 0, 0, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime END_DATE = ZonedDateTime.of(2020, 6, 14, 23, 59, 59, 0, ZoneId.of("UTC"));
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 2;
    private static final String CURRENCY = "USD";
    private static final String NULL_CURRENCY = null;
    private static final String EMPTY_CURRENCY = "";
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);

    private static Price priceEntity;

    private PriceUseCaseMapper priceUseCaseMapper;

    @BeforeEach
    void setUp() {
        priceUseCaseMapper = new PriceUseCaseMapperImpl();
    }

    void getPrice(BigDecimal price, String currency) {
        priceEntity = Price.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .priority(PRIORITY)
                .price(price)
                .currency(currency)
                .build();
    }

    @Test
    void givenValidPrice_whenMappingToPriceResponse_thenAllFieldsAreMappedCorrectly() {
        getPrice(PRICE, CURRENCY);

        final PriceResponseDto response = priceUseCaseMapper.toResponseDto(priceEntity);

        assertEquals(PRICE.doubleValue(), response.price());
        assertEquals(CURRENCY, response.currency());
    }

    @Test
    void givenPriceWithZeroAmount_whenMappingToPriceResponse_thenAmountIsZero() {
        getPrice(BigDecimal.ZERO, CURRENCY);

        final PriceResponseDto response = priceUseCaseMapper.toResponseDto(priceEntity);

        assertEquals(BigDecimal.ZERO.doubleValue(), response.price());
        assertEquals(CURRENCY, response.currency());
    }

    @Test
    void givenPriceWithEmptyCurrency_whenMappingToPriceResponse_thenCurrencyIsEmpty() {
        getPrice(PRICE, EMPTY_CURRENCY);

        final PriceResponseDto response = priceUseCaseMapper.toResponseDto(priceEntity);

        assertEquals(PRICE.doubleValue(), response.price());
        assertEquals(EMPTY_CURRENCY, response.currency());
    }

    @Test
    void givenPriceWithNullCurrency_whenMappingToPriceResponse_thenCurrencyIsNull() {
        getPrice(PRICE, NULL_CURRENCY);

        final PriceResponseDto response = priceUseCaseMapper.toResponseDto(priceEntity);

        assertEquals(PRICE.doubleValue(), response.price());
        assertEquals(NULL_CURRENCY, response.currency());
    }
}
