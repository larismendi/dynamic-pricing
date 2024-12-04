package com.example.dynamicpricing.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.Supplier;

@Configuration
public class InputStreamSupplierConfig {

    @Value("${data.file.path}")
    private String filePath;

    @Bean
    public Supplier<InputStream> inputStreamSupplier() {
        return () -> {
            try {
                final InputStream inputStream = getClass()
                        .getClassLoader()
                        .getResourceAsStream(filePath);

                if (inputStream == null) {
                    throw new FileNotFoundException("File not found: " + filePath);
                }

                return inputStream;
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Error accessing initial data JSON", e);
            }
        };
    }
}
