package com.example.dynamicpricing.infrastructure.configuration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoConfigTest {

    private static final String DB_NAME = "test-database";
    private static final String ILLEGAL_ARGUMENT_EXCEPTION = "MongoDB URI is not configured properly";

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
        mongoConfig.mongoUri = "mongodb://localhost:27017/" + DB_NAME;

        MongoTemplate mongoTemplate = mongoConfig.mongoTemplate();

        assertNotNull(mongoTemplate, "MongoTemplate should not be null");
        assertEquals(DB_NAME, mongoTemplate.getDb().getName(), "Database name should be correct");
    }

    @Test
    void givenInvalidMongoUri_whenMongoTemplateCalled_thenThrowIllegalArgumentException() {
        mongoConfig.mongoUri = "";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, mongoConfig::mongoTemplate);
        assertEquals(ILLEGAL_ARGUMENT_EXCEPTION, exception.getMessage());
    }
}
