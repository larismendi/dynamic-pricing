package com.example.dynamicpricing.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class InputStreamSupplierConfigTest {

    @Test
    void givenValidFile_whenSupplierCalled_thenReturnsInputStream() {
        System.setProperty("data.file.path", "db/migration/initial_data.json");

        ApplicationContext context = new AnnotationConfigApplicationContext(InputStreamSupplierConfig.class);

        Supplier<InputStream> inputStreamSupplier = context.getBean(Supplier.class);

        InputStream inputStream = inputStreamSupplier.get();
        assertNotNull(inputStream, "InputStream should not be null");
    }

    @Test
    void givenInvalidFile_whenSupplierCalled_thenThrowsFileNotFoundException() {
        System.setProperty("data.file.path", "non/existent/path/initial_data.json");

        ApplicationContext context = new AnnotationConfigApplicationContext(InputStreamSupplierConfig.class);

        Supplier<InputStream> inputStreamSupplier = context.getBean(Supplier.class);

        assertThrows(RuntimeException.class, inputStreamSupplier::get);
    }
}
