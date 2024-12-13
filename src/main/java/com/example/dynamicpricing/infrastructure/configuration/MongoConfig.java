package com.example.dynamicpricing.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

    private static final String ILLEGAL_ARGUMENT_EXCEPTION = "MongoDB URI is not configured properly";
    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);
    private static final String PLEASE_SET_THE_MONGO_URI_ENVIRONMENT_VARIABLE =
            "MongoDB URI is not configured. Please set the MONGO_URI environment variable.";

    @Value("${spring.data.mongodb.uri}")
    public String mongoUri;

    @Bean
    public MongoTemplate mongoTemplate() {
        if (mongoUri == null || mongoUri.isEmpty()) {
            logger.error(PLEASE_SET_THE_MONGO_URI_ENVIRONMENT_VARIABLE);
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION);
        }

        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoUri));
    }
}
