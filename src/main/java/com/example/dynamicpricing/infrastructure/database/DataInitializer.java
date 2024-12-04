package com.example.dynamicpricing.infrastructure.database;

import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.repository.MongoPriceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MongoPriceRepository mongoPriceRepository;
    private final Supplier<InputStream> inputStreamSupplier;

    public DataInitializer(MongoPriceRepository mongoPriceRepository, Supplier<InputStream> inputStreamSupplier) {
        this.mongoPriceRepository = mongoPriceRepository;
        this.inputStreamSupplier = inputStreamSupplier;
    }

    @Override
    public void run(String... args) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        try (InputStream inputStream = inputStreamSupplier.get()) {
            final List<PriceEntity> prices = mapper.readValue(inputStream, new TypeReference<>() {
            });
            mongoPriceRepository.deleteAll();
            mongoPriceRepository.saveAll(prices);
            System.out.println("Initial data successfully loaded into MongoDB");
        } catch (IOException e) {
            System.err.println("Error loading initial data: " + e.getMessage());
        }
    }
}
