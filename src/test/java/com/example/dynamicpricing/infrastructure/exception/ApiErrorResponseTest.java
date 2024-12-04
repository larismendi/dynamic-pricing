package com.example.dynamicpricing.infrastructure.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseTest {

    private static final String INITIAL_MESSAGE = "Initial message";
    private static final String INITIAL_DETAILS = "Initial details";
    private static final String NULL_MESSAGE = null;

    private ApiErrorResponse createApiErrorResponse(String message, String details) {
        return new ApiErrorResponse(message, details);
    }

    @Test
    void givenMessageAndDetails_whenCreatingApiErrorResponse_thenFieldsAreSetCorrectly() {
        ApiErrorResponse apiErrorResponse = createApiErrorResponse(INITIAL_MESSAGE, INITIAL_DETAILS);

        assertEquals(INITIAL_MESSAGE, apiErrorResponse.getMessage());
        assertEquals(INITIAL_DETAILS, apiErrorResponse.getDetails());
    }

    @Test
    void givenNullValues_whenCreatingApiErrorResponse_thenFieldsAreNull() {
        ApiErrorResponse apiErrorResponse = createApiErrorResponse(NULL_MESSAGE, NULL_MESSAGE);

        assertNull(apiErrorResponse.getMessage());
        assertNull(apiErrorResponse.getDetails());
    }

    @Test
    void givenSetters_whenSettingFields_thenFieldsAreSetCorrectly() {
        ApiErrorResponse apiErrorResponse = createApiErrorResponse(INITIAL_MESSAGE, INITIAL_DETAILS);
        String expectedMessage = "Another error occurred";
        String expectedDetails = "More details about the error";

        apiErrorResponse.setMessage(expectedMessage);
        apiErrorResponse.setDetails(expectedDetails);

        assertEquals(expectedMessage, apiErrorResponse.getMessage());
        assertEquals(expectedDetails, apiErrorResponse.getDetails());
    }
}
