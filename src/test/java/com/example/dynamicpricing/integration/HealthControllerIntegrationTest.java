package com.example.dynamicpricing.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class HealthControllerIntegrationTest {

    private static final int MONGO_PORT = 27017;
    private static final String APPLICATION_IS_HEALTHY = "Application is healthy";

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
    void givenApplication_whenContextLoads_thenHealthCheckWorks() throws Exception {
        final String healthUrl = "/health";

        final MvcResult result = mockMvc.perform(get(healthUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(APPLICATION_IS_HEALTHY))
                .andReturn();

        assertNotNull(result, "Health check result should not be null");
    }
}
