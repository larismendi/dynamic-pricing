package com.example.dynamicpricing.infrastructure.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class JacksonConfigTest {

    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(JacksonConfig.class);
    }

    @Test
    void givenJacksonConfig_whenObjectMapperBeanCreated_thenTimeZoneIsUTC() {
        ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

        assertEquals(TimeZone.getTimeZone("UTC"), objectMapper.getSerializationConfig().getTimeZone());
    }

    @Test
    void givenJacksonConfig_whenObjectMapperBeanCreated_thenWriteDatesAsTimestampsIsDisabled() {
        ObjectMapper objectMapper = context.getBean(ObjectMapper.class);

        assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }
}

