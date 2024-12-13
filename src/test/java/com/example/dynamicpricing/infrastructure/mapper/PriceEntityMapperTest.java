package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceEntityMapperTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 100;
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(99.99);
    private static final String CURRENCY = "EUR";
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final Instant START_DATE = ZonedDateTime.of(2023, 11, 1, 0, 0, 0, 0, ZONE_ID).toInstant();
    private static final Instant END_DATE = ZonedDateTime.of(2023, 12, 1, 23, 59, 59, 0, ZONE_ID).toInstant();

    private PriceEntityMapper priceEntityMapper;

    @BeforeEach
    void setUp() {
        priceEntityMapper = (entity, zoneId) -> Price.builder()
                .productId(entity.getProductId())
                .brandId(entity.getBrandId())
                .startDate(entity.getStartDate().atZone(ZONE_ID))
                .endDate(entity.getEndDate().atZone(ZONE_ID))
                .priceList(entity.getPriceList())
                .priority(entity.getPriority())
                .price(BigDecimal.valueOf(entity.getPrice()))
                .currency(entity.getCurr())
                .build();
    }

    @Test
    void givenValidPriceEntity_whenMappedToDomain_thenFieldsAreMappedCorrectly() {
        final PriceEntity entity = PriceEntity.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .priority(PRIORITY)
                .price(PRICE.doubleValue())
                .curr(CURRENCY)
                .build();

        final Price price = priceEntityMapper.toDomain(entity, ZONE_ID);

        assertEquals(entity.getProductId(), price.getProductId());
        assertEquals(entity.getBrandId(), price.getBrandId());
        assertEquals(entity.getStartDate().atZone(ZONE_ID), price.getStartDate());
        assertEquals(entity.getEndDate().atZone(ZONE_ID), price.getEndDate());
        assertEquals(entity.getPriceList(), price.getPriceList());
        assertEquals(entity.getPriority(), price.getPriority());
        assertEquals(entity.getPrice(), price.getPrice().doubleValue());
        assertEquals(entity.getCurr(), price.getCurrency());
    }
}
