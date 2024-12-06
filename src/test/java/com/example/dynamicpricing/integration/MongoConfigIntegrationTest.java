package com.example.dynamicpricing.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@ActiveProfiles("integration")
public class MongoConfigIntegrationTest {

    private static final int MONGO_PORT = 27017;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(MONGO_PORT);

    @BeforeAll
    static void setUp() {
        final int mongoPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        final String mongoUri = "mongodb://localhost:" + mongoPort + "/integration-database";

        System.setProperty("spring.data.mongodb.uri", mongoUri);

        mongoDBContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    @Test
    public void testMongoDBContainerIsRunning() {
        assertTrue(mongoDBContainer.isRunning(), "The MongoDB container is running.");

        final String mongoDbUrl = mongoDBContainer.getReplicaSetUrl();
        System.out.println("MongoDB URL: " + mongoDbUrl);
    }
}
