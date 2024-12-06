package com.example.dynamicpricing.infrastructure.configuration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MongoConfigTest {

    private static final int MONGO_PORT = 27017;
    private static final String DB_NAME = "test-database";
    private static final String ILLEGAL_ARGUMENT_EXCEPTION = "MongoDB URI is not configured properly";

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withExposedPorts(MONGO_PORT);

    @InjectMocks
    private MongoConfig mongoConfig;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void givenValidMongoUri_whenMongoTemplateCalled_thenReturnMongoTemplate() {
        final int mongoPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        mongoConfig.mongoUri = "mongodb://localhost:" + mongoPort + "/" + DB_NAME;

        final MongoTemplate mongoTemplate = mongoConfig.mongoTemplate();

        assertNotNull(mongoTemplate, "MongoTemplate should not be null");
        assertEquals(DB_NAME, mongoTemplate.getDb().getName(), "Database name should be correct");
    }

    @Test
    void givenInvalidMongoUri_whenMongoTemplateCalled_thenThrowIllegalArgumentException() {
        mongoConfig.mongoUri = "";

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                mongoConfig::mongoTemplate);
        assertEquals(ILLEGAL_ARGUMENT_EXCEPTION, exception.getMessage());
    }

    @Test
    void givenMongoUriIsNull_whenConfigIsLoaded_thenHandleNullUri() {
        mongoConfig.mongoUri = null;

        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                mongoConfig::mongoTemplate);
        assertEquals(ILLEGAL_ARGUMENT_EXCEPTION, exception.getMessage());
    }
}
