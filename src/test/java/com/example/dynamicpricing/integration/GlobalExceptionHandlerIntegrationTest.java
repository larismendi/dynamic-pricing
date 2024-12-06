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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("integration")
class GlobalExceptionHandlerIntegrationTest {

    private static final int MONGO_PORT = 27017;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
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
    void givenInvalidJsonFormat_whenParsingFails_thenReturnBadRequestWithError() throws Exception {
        final String invalidRequestJson = """
                {
                    "productId": -1,
                    "brandId": null,
                    "applicationDate": "invalid-date"
                }
                """;

        final ResultActions result = mockMvc.perform(post("/api/price")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson));

        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Invalid JSON format")));
    }

    @Test
    void givenInvalidPriceRequest_whenCalculatePrice_thenReturnsBadRequest() throws Exception {
        final String invalidRequest = """
                    {
                        "productId": -1,
                        "brandId": 0,
                        "applicationDate": "2024-12-02T10:30:00"
                    }
                """;

        mockMvc.perform(post("/api/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.productId").value("Product ID must be positive"))
                .andExpect(jsonPath("$.brandId").value("Brand ID must be positive"));
    }
}
