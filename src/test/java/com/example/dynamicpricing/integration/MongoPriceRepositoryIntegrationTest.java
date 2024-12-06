package com.example.dynamicpricing.integration;

import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.repository.MongoPriceRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@ActiveProfiles("integration")
public class MongoPriceRepositoryIntegrationTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final int SECONDS_TO_ADD = 10000;
    private static final LocalDateTime START_DATE = LocalDateTime.now().minusSeconds(SECONDS_TO_ADD)
            .truncatedTo(ChronoUnit.SECONDS);
    private static final LocalDateTime END_DATE = LocalDateTime.now().plusSeconds(SECONDS_TO_ADD)
            .truncatedTo(ChronoUnit.SECONDS);
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.now()
            .truncatedTo(ChronoUnit.SECONDS);
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);
    private static final String CURRENCY = "USD";
    private static final int BRAND_ID_2 = 2;
    private static final int PRODUCT_ID_2 = 2;
    public static final int SECONDS = 5000;
    private static final LocalDateTime START_DATE_2 = LocalDateTime.now().minusSeconds(SECONDS)
            .truncatedTo(ChronoUnit.SECONDS);
    private static final LocalDateTime END_DATE_2 = LocalDateTime.now().plusSeconds(SECONDS)
            .truncatedTo(ChronoUnit.SECONDS);
    private static final int PRIORITY_2 = 2;
    private static final BigDecimal PRICE_2 = BigDecimal.valueOf(120);
    private static final int MONGO_PORT = 27017;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
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
    public static void tearDown() {
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

        final Instant applicationDate = APPLICATION_DATE.atZone(ZoneId.of("UTC")).toInstant()
                .truncatedTo(ChronoUnit.MINUTES);

        final List<PriceEntity> result = mongoPriceRepository
                .findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        PRODUCT_ID, BRAND_ID, applicationDate, applicationDate);

        assertEquals(1, result.size(), "Expected 1 matching price entity");
        assertEquals(priceEntity1.getProductId(), result.get(0).getProductId(),
                "Expected the correct price entity to be returned");
    }

    @Test
    void givenNoMatchingPriceEntities_whenFindByCriteria_thenReturnEmptyList() {
        final Instant startDate = Instant.now().minusSeconds(SECONDS_TO_ADD);
        final Instant endDate = Instant.now().plusSeconds(SECONDS_TO_ADD);

        final List<PriceEntity> result = mongoPriceRepository
                .findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        PRODUCT_ID, BRAND_ID, startDate, endDate);

        assertEquals(0, result.size(), "Expected no matching price entities");
    }
}
