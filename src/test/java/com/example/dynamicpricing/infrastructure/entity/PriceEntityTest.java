package com.example.dynamicpricing.infrastructure.entity;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceEntityTest {

    public static final String CURR = "USD";
    public static final int BRAND_ID = 1;
    public static final int PRIORITY = 1;
    public static final int PRODUCT_ID = 2001;
    public static final int PRICE_LIST = 101;
    public static final double PRICE = 99.99;

    @Test
    void givenPriceEntity_whenBuilt_thenAllFieldsAreSetCorrectly() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(1);

        PriceEntity entity = PriceEntity.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(startDate)
                .endDate(endDate)
                .priceList(PRICE_LIST)
                .priority(PRIORITY)
                .price(PRICE)
                .curr(CURR)
                .build();

        assertAll(
                () -> assertEquals(PRODUCT_ID, entity.getProductId(), "Product ID should match"),
                () -> assertEquals(BRAND_ID, entity.getBrandId(), "Brand ID should match"),
                () -> assertEquals(startDate, entity.getStartDate(), "Start date should match"),
                () -> assertEquals(endDate, entity.getEndDate(), "End date should match"),
                () -> assertEquals(PRICE_LIST, entity.getPriceList(), "Price list should match"),
                () -> assertEquals(PRIORITY, entity.getPriority(), "Priority should match"),
                () -> assertEquals(PRICE, entity.getPrice(), "Price should match"),
                () -> assertEquals(CURR, entity.getCurr(), "Currency should match")
        );
    }

    @Test
    void givenPriceEntityAnnotation_whenChecked_thenDocumentAnnotationExists() {
        Class<PriceEntity> clazz = PriceEntity.class;

        Document documentAnnotation = clazz.getAnnotation(Document.class);

        assertNotNull(documentAnnotation, "Document annotation should be present");
        assertEquals("prices", documentAnnotation.collection(), "Collection name should be 'prices'");
    }
}
