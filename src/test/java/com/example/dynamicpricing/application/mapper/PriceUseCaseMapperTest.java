package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceUseCaseMapperTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 6, 14, 23, 59, 59);
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 2;
    private static final String CURRENCY = "USD";
    private static final String NULL_CURRENCY = null;
    private static final String EMPTY_CURRENCY = "";
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);

    private static Price priceEntity;

    private PriceUseCaseMapper priceUseCaseMapper;

    @BeforeEach
    public void setUp() {
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
    public void givenValidPrice_whenMappingToPriceResponse_thenAllFieldsAreMappedCorrectly() {
        getPrice(PRICE, CURRENCY);

        PriceResponse response = priceUseCaseMapper.toResponse(priceEntity);

        assertEquals(PRICE.doubleValue(), response.getPrice());
        assertEquals(CURRENCY, response.getCurrency());
    }

    @Test
    public void givenPriceWithZeroAmount_whenMappingToPriceResponse_thenAmountIsZero() {
        getPrice(BigDecimal.ZERO, CURRENCY);

        PriceResponse response = priceUseCaseMapper.toResponse(priceEntity);

        assertEquals(BigDecimal.ZERO.doubleValue(), response.getPrice());
        assertEquals(CURRENCY, response.getCurrency());
    }

    @Test
    public void givenPriceWithEmptyCurrency_whenMappingToPriceResponse_thenCurrencyIsEmpty() {
        getPrice(PRICE, EMPTY_CURRENCY);

        PriceResponse response = priceUseCaseMapper.toResponse(priceEntity);

        assertEquals(PRICE.doubleValue(), response.getPrice());
        assertEquals(EMPTY_CURRENCY, response.getCurrency());
    }

    @Test
    public void givenPriceWithNullCurrency_whenMappingToPriceResponse_thenCurrencyIsNull() {
        getPrice(PRICE, NULL_CURRENCY);

        PriceResponse response = priceUseCaseMapper.toResponse(priceEntity);

        assertEquals(PRICE.doubleValue(), response.getPrice());
        assertEquals(NULL_CURRENCY, response.getCurrency());
    }
}

