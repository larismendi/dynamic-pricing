package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PriceUseCaseMapperImplTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final ZonedDateTime START_DATE = ZonedDateTime.of(2020, 6, 14, 0, 0, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime END_DATE = ZonedDateTime.of(2020, 6, 14, 23, 59, 59, 0, ZoneId.of("UTC"));
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 2;
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);
    private static final double DELTA = 0.01;

    private final PriceUseCaseMapperImpl priceUseCaseMapper = new PriceUseCaseMapperImpl();

    @Test
    void givenValidInputs_whenCreatingPriceResponse_thenAllFieldsAreSetCorrectly() {
        final Price mockPrice = Price.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priority(PRIORITY)
                .price(PRICE)
                .currency(CURRENCY)
                .build();

        final PriceResponse response = priceUseCaseMapper.toResponse(mockPrice);

        assertNotNull(response);
        assertEquals(PRODUCT_ID, response.getProductId());
        assertEquals(BRAND_ID, response.getBrandId());
        assertEquals(PRICE_LIST, response.getPriceList());
        assertEquals(START_DATE.toString(), response.getStartDate());
        assertEquals(END_DATE.toString(), response.getEndDate());
        assertEquals(PRICE.doubleValue(), response.getPrice(), DELTA);
        assertEquals(CURRENCY, response.getCurrency());
    }
}
