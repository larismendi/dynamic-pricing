package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.domain.model.Price;
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

        final PriceResponseDto response = priceUseCaseMapper.toResponseDto(mockPrice);

        assertNotNull(response);
        assertEquals(PRODUCT_ID, response.productId());
        assertEquals(BRAND_ID, response.brandId());
        assertEquals(PRICE_LIST, response.priceList());
        assertEquals(START_DATE.toString(), response.startDate());
        assertEquals(END_DATE.toString(), response.endDate());
        assertEquals(PRICE.doubleValue(), response.price(), DELTA);
        assertEquals(CURRENCY, response.currency());
    }
}
