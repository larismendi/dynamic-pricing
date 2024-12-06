package com.example.dynamicpricing.infrastructure.database;

import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.repository.MongoPriceRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;


@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String INITIAL_DATA_SUCCESSFULLY_LOADED_INTO_MONGO_DB
            = "Initial data successfully loaded into MongoDB";
    private static final String ERROR_LOADING_INITIAL_DATA = "Error loading initial data: {}";

    private final MongoPriceRepository mongoPriceRepository;
    private final Supplier<InputStream> inputStreamSupplier;

    public DataInitializer(MongoPriceRepository mongoPriceRepository, Supplier<InputStream> inputStreamSupplier) {
        this.mongoPriceRepository = mongoPriceRepository;
        this.inputStreamSupplier = inputStreamSupplier;
    }

    @Override
    public void run(String... args) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        try {
            InputStream inputStream = inputStreamSupplier.get();
            final List<PriceEntity> prices = mapper.readValue(inputStream, new TypeReference<>() {
            });
            mongoPriceRepository.deleteAll();
            mongoPriceRepository.saveAll(prices);
            logger.info(INITIAL_DATA_SUCCESSFULLY_LOADED_INTO_MONGO_DB);
        } catch (IOException e) {
            logger.error(ERROR_LOADING_INITIAL_DATA, e.getMessage(), e);
            throw e;
        }
    }
}
