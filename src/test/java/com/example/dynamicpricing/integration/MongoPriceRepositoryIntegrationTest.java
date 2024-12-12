package com.example.dynamicpricing.integration;

import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.repository.MongoPriceRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration")
class MongoPriceRepositoryIntegrationTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final int SECONDS_TO_ADD = 10000;
    private static final Instant START_DATE = ZonedDateTime.now().minusSeconds(SECONDS_TO_ADD).toInstant();
    private static final Instant END_DATE = ZonedDateTime.now().plusSeconds(SECONDS_TO_ADD).toInstant();
    private static final ZonedDateTime APPLICATION_DATE = ZonedDateTime.now();
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);
    private static final String CURRENCY = "USD";
    private static final int BRAND_ID_2 = 2;
    private static final int PRODUCT_ID_2 = 2;
    private static final int SECONDS = 5000;
    private static final Instant START_DATE_2 = ZonedDateTime.now().minusSeconds(SECONDS).toInstant();
    private static final Instant END_DATE_2 = ZonedDateTime.now().plusSeconds(SECONDS).toInstant();
    private static final int PRIORITY_2 = 2;
    private static final BigDecimal PRICE_2 = BigDecimal.valueOf(120);
    private static final int MONGO_PORT = 27017;
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "priority"));

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(MONGO_PORT);

    @Autowired
    private MongoPriceRepository mongoPriceRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeAll
    static void setUp() {
        final int mongoPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        final String mongoUri = "mongodb://localhost:" + mongoPort + "/integration-database";

        System.setProperty("spring.data.mongodb.uri", mongoUri);

        mongoDBContainer.start();
    }

    @AfterEach
    void cleanDatabase() {
        mongoTemplate.getDb().drop();
    }

    @AfterAll
    static void tearDown() {
        mongoDBContainer.stop();
    }

    @Test
    void givenValidPriceEntities_whenFindByCriteria_thenReturnMatchingEntities() {
        final PriceEntity priceEntity1 = PriceEntity.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST)
                .priority(PRIORITY)
                .price(PRICE.doubleValue())
                .curr(CURRENCY)
                .build();
        final PriceEntity priceEntity2 = PriceEntity.builder()
                .productId(PRODUCT_ID_2)
                .brandId(BRAND_ID_2)
                .startDate(START_DATE_2)
                .endDate(END_DATE_2)
                .priceList(PRICE_LIST)
                .priority(PRIORITY_2)
                .price(PRICE_2.doubleValue())
                .curr(CURRENCY)
                .build();
        mongoPriceRepository.save(priceEntity1);
        mongoPriceRepository.save(priceEntity2);

        final Instant applicationDate = APPLICATION_DATE.toInstant();

        final List<PriceEntity> result = mongoPriceRepository
                .findTopByProductIdAndBrandIdAndApplicationDate(
                        PRODUCT_ID, BRAND_ID, applicationDate, PAGE_REQUEST);

        assertEquals(1, result.size(), "Expected 1 matching price entity");
        assertEquals(priceEntity1.getProductId(), result.get(0).getProductId(),
                "Expected the correct price entity to be returned");
    }

    @Test
    void givenNoMatchingPriceEntities_whenFindByCriteria_thenReturnEmptyList() {
        final Instant startDate = Instant.now().minusSeconds(SECONDS_TO_ADD);

        final List<PriceEntity> result = mongoPriceRepository
                .findTopByProductIdAndBrandIdAndApplicationDate(
                        PRODUCT_ID, BRAND_ID, startDate, PAGE_REQUEST);

        assertEquals(0, result.size(), "Expected no matching price entities");
    }
}
