package com.example.dynamicpricing.infrastructure.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PriceRequestTests {

    private static final int VALID_ID = 1;
    private static final int NEGATIVE_ID = -1;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.now();
    private static final Logger logger = LoggerFactory.getLogger(PriceRequestTests.class);
    private static final LocalDateTime NULL_APPLICATION_DATE = null;

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        } catch (Exception e) {
            logger.error("Error initializing mocks: ", e);
        }
    }

    @Test
    void givenValidPriceRequest_whenValidated_thenNoViolations() {
        final PriceRequest priceRequest = PriceRequest.builder()
                .productId(VALID_ID)
                .brandId(VALID_ID)
                .applicationDate(APPLICATION_DATE)
                .build();

        final Set<ConstraintViolation<PriceRequest>> violations = validator.validate(priceRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void givenNegativeProductId_whenValidated_thenProductIdViolation() {
        final PriceRequest priceRequest = PriceRequest.builder()
                .productId(NEGATIVE_ID)
                .brandId(VALID_ID)
                .applicationDate(APPLICATION_DATE)
                .build();

        final Set<ConstraintViolation<PriceRequest>> violations = validator.validate(priceRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("productId")));
    }

    @Test
    void givenNegativeBrandId_whenValidated_thenBrandIdViolation() {
        final PriceRequest priceRequest = PriceRequest.builder()
                .productId(VALID_ID)
                .brandId(NEGATIVE_ID)
                .applicationDate(APPLICATION_DATE)
                .build();

        final Set<ConstraintViolation<PriceRequest>> violations = validator.validate(priceRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("brandId")));
    }

    @Test
    void givenNullApplicationDate_whenValidated_thenApplicationDateViolation() {
        final PriceRequest priceRequest = PriceRequest.builder()
                .productId(VALID_ID)
                .brandId(VALID_ID)
                .applicationDate(NULL_APPLICATION_DATE)
                .build();

        final Set<ConstraintViolation<PriceRequest>> violations = validator.validate(priceRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("applicationDate")));
    }
}
