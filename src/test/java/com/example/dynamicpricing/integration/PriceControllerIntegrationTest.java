package com.example.dynamicpricing.integration;

import com.example.dynamicpricing.infrastructure.controller.request.PriceRequest;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.repository.MongoPriceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private static final LocalDateTime START_DATE = LocalDateTime.of(2024, 12, 2, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2024, 12, 2, 23, 59);
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2024, 12, 2, 12, 0);
    private static final int PRICE_LIST = 1;
    private static final int PRICE_LIST_2 = 2;
    private static final int PRIORITY = 1;
    private static final int PRIORITY_2 = 2;
    private static final BigDecimal PRICE = BigDecimal.valueOf(100.0);
    private static final BigDecimal PRICE_2 = BigDecimal.valueOf(120.0);
    private static final String CURRENCY = "EUR";
    private static final String INVALID_PRODUCT_ID_MESSAGE = "Product ID must be positive";
    private static final String INVALID_APPLICATION_DATE_MESSAGE_CONTAINS = "Invalid JSON format: JSON parse error:";

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
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
    public static void tearDown() {
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
        final PriceRequest priceRequest = PriceRequest.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .applicationDate(APPLICATION_DATE)
                .build();

        final ResponseEntity<PriceResponse> response = restTemplate.postForEntity(
                "/api/price", priceRequest, PriceResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(PRODUCT_ID, response.getBody().getProductId());
        assertEquals(BRAND_ID, response.getBody().getBrandId());
        assertEquals(PRICE_2.doubleValue(), response.getBody().getPrice());
    }

    @Test
    void givenInvalidProductIdPriceRequest_whenCalculatePrice_thenReturnBadRequest() {
        final PriceRequest invalidRequest = PriceRequest.builder()
                .productId(BAD_PRODUCT_ID)
                .brandId(BRAND_ID)
                .applicationDate(LocalDateTime.now())
                .build();

        final ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                "/api/price",
                HttpMethod.POST,
                new HttpEntity<>(invalidRequest),
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(INVALID_PRODUCT_ID_MESSAGE, response.getBody().get("productId"));
    }

    @Test
    public void givenInvalidApplicationDatePriceRequest_whenCalculatePrice_thenReturnBadRequest() {
        final String validJson = """
                {
                    "productId": 101,
                    "brandId": 1,
                    "applicationDate": "invalid-datetime"
                }
                """;

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final HttpEntity<String> requestEntity = new HttpEntity<>(validJson, headers);

        final ResponseEntity<String> response = restTemplate.exchange(
                "/api/price",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(INVALID_APPLICATION_DATE_MESSAGE_CONTAINS));
    }
}
