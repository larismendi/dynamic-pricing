package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private static final LocalDateTime START_DATE = LocalDateTime.of(2023, 11, 1, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2023, 12, 1, 23, 59, 59);

    private PriceEntityMapperImpl priceEntityMapper;

    @BeforeEach
    void setUp() {
        priceEntityMapper = new PriceEntityMapperImpl();
    }

    @Test
    void givenValidPriceEntity_whenToDomain_thenReturnMappedPrice() {
        PriceEntity priceEntity = PriceEntity.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .price(PRICE.doubleValue())
                .curr(CURRENCY)
                .priority(PRIORITY)
                .build();

        Price result = priceEntityMapper.toDomain(priceEntity);

        assertNotNull(result, "The result should not be null");
        assertEquals(priceEntity.getProductId(), result.getProductId(), "Product ID should match");
        assertEquals(priceEntity.getBrandId(), result.getBrandId(), "Brand ID should match");
        assertEquals(priceEntity.getPriceList(), result.getPriceList(), "Price list should match");
        assertEquals(priceEntity.getStartDate(), result.getStartDate(), "Start date should match");
        assertEquals(priceEntity.getEndDate(), result.getEndDate(), "End date should match");
        assertEquals(BigDecimal.valueOf(priceEntity.getPrice()), result.getPrice(), "Price should match");
        assertEquals(priceEntity.getCurr(), result.getCurrency(), "Currency should match");
        assertEquals(priceEntity.getPriority(), result.getPriority(), "Priority should match");
    }
}
