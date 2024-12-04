package com.example.dynamicpricing.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@ActiveProfiles("test")
public class MongoConfigIntegrationTest {

    private static final int MONGO_PORT = 27017;
    public static final boolean REUSABLE = true;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4")
            .withExposedPorts(MONGO_PORT)
            .withReuse(REUSABLE);

    @BeforeAll
    static void setUp() {
        int mongoPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        String mongoUri = "mongodb://localhost:" + mongoPort + "/test-database";

        System.setProperty("spring.data.mongodb.uri", mongoUri);

        mongoDBContainer.start();
    }

    @Test
    public void testMongoDBContainerIsRunning() {
        assertTrue(mongoDBContainer.isRunning(), "The MongoDB container is running.");

        String mongoDbUrl = mongoDBContainer.getReplicaSetUrl();
        System.out.println("MongoDB URL: " + mongoDbUrl);
    }
}
