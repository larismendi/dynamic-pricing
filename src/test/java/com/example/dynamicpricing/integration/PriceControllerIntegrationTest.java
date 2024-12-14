package com.example.dynamicpricing.integration;

import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.repository.MongoPriceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration")
class PriceControllerIntegrationTest {

    private static final int MONGO_PORT = 27017;
    private static final int BRAND_ID = 1;
    private static final int BAD_PRODUCT_ID = -1;
    private static final int PRODUCT_ID = 101;
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final Instant START_DATE = ZonedDateTime.of(2024, 12, 2, 0, 0, 0, 0, ZONE_ID).toInstant();
    private static final Instant END_DATE = ZonedDateTime.of(2024, 12, 2, 23, 59, 59, 0, ZONE_ID).toInstant();
    private static final ZonedDateTime APPLICATION_DATE = ZonedDateTime.of(2024, 12, 2, 12, 0, 0, 0, ZONE_ID);
    private static final int PRICE_LIST = 1;
    private static final int PRICE_LIST_2 = 2;
    private static final int PRIORITY = 1;
    private static final int PRIORITY_2 = 2;
    private static final BigDecimal PRICE = BigDecimal.valueOf(100.0);
    private static final BigDecimal PRICE_2 = BigDecimal.valueOf(120.0);
    private static final String CURRENCY = "EUR";
    private static final String INVALID_PRODUCT_ID_MESSAGE = "Product ID must be positive";
    private static final String INVALID_DATETIME = "invalid-datetime";
    private static final String INVALID_VALUE_FOR_APPLICATION_DATE = "Invalid value for 'applicationDate'";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(MONGO_PORT);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoPriceRepository mongoPriceRepository;

    @BeforeAll
    static void setUpBeforeAll() {
        final int mongoPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        final String mongoUri = "mongodb://localhost:" + mongoPort + "/integration-database";

        System.setProperty("spring.data.mongodb.uri", mongoUri);

        mongoDBContainer.start();
    }

    @BeforeEach
    void setUp() {
        generateData();
    }

    @AfterEach
    void cleanUpDatabase() {
        mongoTemplate.getDb().drop();
    }

    @AfterAll
    static void tearDown() {
        mongoDBContainer.stop();
    }

    void generateData() {
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
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priceList(PRICE_LIST_2)
                .priority(PRIORITY_2)
                .price(PRICE_2.doubleValue())
                .curr(CURRENCY)
                .build();
        mongoPriceRepository.save(priceEntity1);
        mongoPriceRepository.save(priceEntity2);
    }

    @Test
    void givenValidPriceRequest_whenCalculatePrice_thenReturnValidPriceResponse() {
        final String url = "/api/price?productId=" + PRODUCT_ID + "&brandId=" + BRAND_ID + "&applicationDate="
                + APPLICATION_DATE;

        final ResponseEntity<PriceResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                PriceResponse.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(PRODUCT_ID, response.getBody().getProductId());
        assertEquals(BRAND_ID, response.getBody().getBrandId());
        assertEquals(PRICE_2.doubleValue(), response.getBody().getPrice());
    }

    @Test
    void givenInvalidProductIdPriceRequest_whenCalculatePrice_thenReturnBadRequest() {
        final String url = "/api/price?productId=" + BAD_PRODUCT_ID + "&brandId=" + BRAND_ID + "&applicationDate="
                + APPLICATION_DATE;

        final ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(INVALID_PRODUCT_ID_MESSAGE, response.getBody().get("productId"));
    }

    @Test
    void givenInvalidApplicationDatePriceRequest_whenCalculatePrice_thenReturnBadRequest() {
        final String url = "/api/price?productId=" + PRODUCT_ID + "&brandId=" + BRAND_ID + "&applicationDate="
                + INVALID_DATETIME;

        final ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(INVALID_VALUE_FOR_APPLICATION_DATE));
    }
}
