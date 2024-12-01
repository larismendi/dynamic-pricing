package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
public class PriceUseCaseMapperImplTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 6, 14, 23, 59, 59);
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 2;
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);

    @InjectMocks
    private PriceUseCaseMapperImpl priceUseCaseMapper;

    @Test
    public void givenValidInputs_whenCreatingPriceResponse_thenAllFieldsAreSetCorrectly() {
        Price mockPrice = Price.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priority(PRIORITY)
                .price(PRICE)
                .currency(CURRENCY)
                .build();

        PriceResponse response = priceUseCaseMapper.toResponse(mockPrice);

        assertNotNull(response);
        assertEquals(PRODUCT_ID, response.getProductId());
        assertEquals(BRAND_ID, response.getBrandId());
        assertEquals(PRICE_LIST, response.getPriceList());
        assertEquals(START_DATE.toString(), response.getStartDate());
        assertEquals(END_DATE.toString(), response.getEndDate());
        assertEquals(PRICE.doubleValue(), response.getPrice(), 0.01);
        assertEquals(CURRENCY, response.getCurrency());
    }
}
