package com.example.dynamicpricing.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class GlobalExceptionHandlerIntegrationTest {

    private static final int MONGO_PORT = 27017;
    private static final String PRODUCT_ID_IS_REQUIRED = "Product ID is required";
    private static final String BRAND_ID_IS_REQUIRED = "Brand ID is required";
    private static final String PRODUCT_ID_MUST_BE_POSITIVE = "Product ID must be positive";
    private static final String BRAND_ID_MUST_BE_POSITIVE = "Brand ID must be positive";
    private static final String INVALID_VALUE_PROVIDED_FOR_APPLICATION_DATE =
            "Invalid value provided for 'applicationDate'";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(MONGO_PORT);

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUp() {
        final int mongoPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        final String mongoUri = "mongodb://localhost:" + mongoPort + "/integration-database";

        System.setProperty("spring.data.mongodb.uri", mongoUri);

        mongoDBContainer.start();
    }

    @Test
    void givenMissingFieldsRequest_whenParsingFails_thenReturnBadRequestWithError() throws Exception {
        final String url = "/api/price?productId=&brandId=&applicationDate=2024-12-02T10:30:00Z";

        final ResultActions result = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
        );

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.productId").value(org.hamcrest.Matchers.containsString(PRODUCT_ID_IS_REQUIRED)))
                .andExpect(jsonPath("$.brandId").value(org.hamcrest.Matchers.containsString(BRAND_ID_IS_REQUIRED)));
    }

    @Test
    void givenInvalidPriceRequest_whenCalculatePrice_thenReturnsBadRequest() throws Exception {
        final String url = "/api/price?productId=0&brandId=0&applicationDate=2024-12-02T10:30:00Z";

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID_MUST_BE_POSITIVE))
                .andExpect(jsonPath("$.brandId").value(BRAND_ID_MUST_BE_POSITIVE));
    }

    @Test
    void givenInvalidApplicationDate_whenCalculatePrice_thenReturnsBadRequest() throws Exception {
        final String url = "/api/price?productId=1&brandId=1&applicationDate=invalid-date";

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.applicationDate").value(
                        org.hamcrest.Matchers.containsString(INVALID_VALUE_PROVIDED_FOR_APPLICATION_DATE)));
    }
}
