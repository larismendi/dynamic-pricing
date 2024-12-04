package com.example.dynamicpricing.infrastructure.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthControllerTest {

    public static final String APPLICATION_IS_HEALTHY = "Application is healthy";
    
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HealthController healthController = new HealthController();
        mockMvc = MockMvcBuilders.standaloneSetup(healthController).build();
    }

    @Test
    void checkHealth_shouldReturnHealthyMessage() throws Exception {
        mockMvc.perform(get("/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(APPLICATION_IS_HEALTHY));
    }
}
