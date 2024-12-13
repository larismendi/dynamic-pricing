package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PriceEntityMapperImplTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 100;
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(99.99);
    private static final String CURRENCY = "EUR";
    private static final Instant START_DATE = ZonedDateTime.of(2023, 11, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
    private static final Instant END_DATE = ZonedDateTime.of(2023, 12, 1, 23, 59, 59, 0, ZoneId.of("UTC")).toInstant();

    private PriceEntityMapperImpl priceEntityMapper;

    @BeforeEach
    void setUp() {
        priceEntityMapper = new PriceEntityMapperImpl();
    }

    @Test
    void givenValidPriceEntity_whenToDomain_thenReturnMappedPrice() {
        final PriceEntity priceEntity = PriceEntity.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .price(PRICE.doubleValue())
                .curr(CURRENCY)
                .priority(PRIORITY)
                .build();

        final Price result = priceEntityMapper.toDomain(priceEntity, ZoneId.of("UTC"));

        assertNotNull(result);
        assertEquals(priceEntity.getProductId(), result.getProductId());
        assertEquals(priceEntity.getBrandId(), result.getBrandId());
        assertEquals(priceEntity.getPriceList(), result.getPriceList());
        assertEquals(priceEntity.getStartDate(), result.getStartDate().toInstant());
        assertEquals(priceEntity.getEndDate(), result.getEndDate().toInstant());
        assertEquals(BigDecimal.valueOf(priceEntity.getPrice()), result.getPrice());
        assertEquals(priceEntity.getCurr(), result.getCurrency());
        assertEquals(priceEntity.getPriority(), result.getPriority());
    }
}
