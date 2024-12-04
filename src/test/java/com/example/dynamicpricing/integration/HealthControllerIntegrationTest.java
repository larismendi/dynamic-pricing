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
@ActiveProfiles("test")
public class HealthControllerIntegrationTest {

    private static final int MONGO_PORT = 27017;
    private static final String APPLICATION_IS_HEALTHY = "Application is healthy";
    public static final boolean REUSABLE = true;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4")
            .withExposedPorts(MONGO_PORT)
            .withReuse(REUSABLE);

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUp() {
        int mongoPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        String mongoUri = "mongodb://localhost:" + mongoPort + "/test-database";

        System.setProperty("spring.data.mongodb.uri", mongoUri);

        mongoDBContainer.start();
    }

    @Test
    void givenApplication_whenContextLoads_thenHealthCheckWorks() throws Exception {
        String healthUrl = "/health";

        MvcResult result = mockMvc.perform(get(healthUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(APPLICATION_IS_HEALTHY))
                .andReturn();

        assertNotNull(result, "Health check result should not be null");
    }
}
