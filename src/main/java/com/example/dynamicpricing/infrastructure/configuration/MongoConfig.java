package com.example.dynamicpricing.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoConfig {

    private static final String ILLEGAL_ARGUMENT_EXCEPTION = "MongoDB URI is not configured properly";

    @Value("${spring.data.mongodb.uri}")
    public String mongoUri;

    @Bean
    public MongoTemplate mongoTemplate() {
        if (mongoUri == null || mongoUri.isEmpty()) {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT_EXCEPTION);
        }

        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoUri));
    }
}
