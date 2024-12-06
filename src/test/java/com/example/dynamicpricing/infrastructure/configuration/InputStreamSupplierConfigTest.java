package com.example.dynamicpricing.infrastructure.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class InputStreamSupplierConfigTest {

    private static final String NON_EXISTENT_PATH_INITIAL_DATA_JSON = "non/existent/path/initial_data.json";
    private static final String DB_MIGRATION_INITIAL_DATA_JSON = "db/migration/initial_data.json";

    void setFilePath(String newFilePath) {
        System.setProperty("data.file.path", newFilePath);
    }

    @Test
    void givenValidFile_whenSupplierCalled_thenReturnsInputStream() {
        setFilePath(DB_MIGRATION_INITIAL_DATA_JSON);

        final ApplicationContext context = new AnnotationConfigApplicationContext(InputStreamSupplierConfig.class);

        final Supplier<InputStream> inputStreamSupplier = context.getBean(Supplier.class);

        final InputStream inputStream = inputStreamSupplier.get();
        assertNotNull(inputStream, "InputStream should not be null");
    }

    @Test
    void givenInvalidFile_whenSupplierCalled_thenThrowsFileNotFoundException() {
        setFilePath(NON_EXISTENT_PATH_INITIAL_DATA_JSON);

        final ApplicationContext context = new AnnotationConfigApplicationContext(InputStreamSupplierConfig.class);

        final Supplier<InputStream> inputStreamSupplier = context.getBean(Supplier.class);

        assertThrows(RuntimeException.class, inputStreamSupplier::get);

        setFilePath(DB_MIGRATION_INITIAL_DATA_JSON);
    }
}
