package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceEntityMapperTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 100;
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(99.99);
    private static final String CURRENCY = "EUR";
    private static final LocalDateTime START_DATE = LocalDateTime.of(2023, 11, 1, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2023, 12, 1, 23, 59, 59);

    private PriceEntityMapper priceEntityMapper;

    @BeforeEach
    void setUp() {
        priceEntityMapper = entity -> Price.builder()
                .productId(entity.getProductId())
                .brandId(entity.getBrandId())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .priceList(entity.getPriceList())
                .priority(entity.getPriority())
                .price(BigDecimal.valueOf(entity.getPrice()))
                .currency(entity.getCurr())
                .build();
    }

    @Test
    void givenValidPriceEntity_whenMappedToDomain_thenFieldsAreMappedCorrectly() {
        PriceEntity entity = PriceEntity.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .priority(PRIORITY)
                .price(PRICE.doubleValue())
                .curr(CURRENCY)
                .build();

        Price price = priceEntityMapper.toDomain(entity);

        assertEquals(entity.getProductId(), price.getProductId());
        assertEquals(entity.getBrandId(), price.getBrandId());
        assertEquals(entity.getStartDate(), price.getStartDate());
        assertEquals(entity.getEndDate(), price.getEndDate());
        assertEquals(entity.getPriceList(), price.getPriceList());
        assertEquals(entity.getPriority(), price.getPriority());
        assertEquals(entity.getPrice(), price.getPrice().doubleValue());
        assertEquals(entity.getCurr(), price.getCurrency());
    }
}

