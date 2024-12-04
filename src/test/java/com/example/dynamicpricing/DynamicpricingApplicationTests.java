package com.example.dynamicpricing;

import com.example.dynamicpricing.application.usecase.DeterminePriceUseCase;
import com.example.dynamicpricing.domain.repository.PriceRepository;
import com.example.dynamicpricing.domain.service.PriceService;
import com.example.dynamicpricing.infrastructure.controller.PriceController;
import com.example.dynamicpricing.infrastructure.exception.GlobalExceptionHandler;
import com.example.dynamicpricing.infrastructure.mapper.PriceEntityMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DynamicPricingApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class DynamicPricingApplicationTests {

    private static final int MONGO_PORT = 27017;
    public static final String APPLICATION_IS_HEALTHY = "Application is healthy";
    public static final boolean REUSABLE = true;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4")
            .withExposedPorts(MONGO_PORT)
            .withReuse(REUSABLE);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setUpBeforeAll() {
        int mongoPort = mongoDBContainer.getMappedPort(MONGO_PORT);
        String mongoUri = "mongodb://localhost:" + mongoPort + "/test-database";

        System.setProperty("spring.data.mongodb.uri", mongoUri);

        mongoDBContainer.start();
    }

    @Test
    void givenApplication_whenContextLoads_thenHealthCheckWorks() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(APPLICATION_IS_HEALTHY));
    }

    @Test
    void givenApplication_whenContextLoads_thenRequiredBeansArePresent() {
        assertNotNull(applicationContext.getBean(DynamicPricingApplication.class),
                "DynamicPricingApplication bean should be loaded");
        assertNotNull(applicationContext.getBean(PriceController.class),
                "PriceController bean should be loaded");
        assertNotNull(applicationContext.getBean(PriceService.class),
                "PriceService bean should be loaded");
        assertNotNull(applicationContext.getBean(PriceEntityMapper.class),
                "PriceEntityMapper bean should be loaded");
        assertNotNull(applicationContext.getBean(GlobalExceptionHandler.class),
                "GlobalExceptionHandler bean should be loaded");
        assertNotNull(applicationContext.getBean(DeterminePriceUseCase.class),
                "DeterminePriceUseCase bean should be loaded");
        assertNotNull(applicationContext.getBean(PriceRepository.class),
                "DeterminePriceUseCase bean should be loaded");
    }

    @Test
    void givenContext_whenBeanRequested_thenBeanExists() {
        String expectedBeanName = "determinePriceUseCaseImpl";

        boolean beanExists = applicationContext.containsBean(expectedBeanName);

        assertTrue(beanExists, "The bean " + expectedBeanName + " should be available in the context.");
    }

}
