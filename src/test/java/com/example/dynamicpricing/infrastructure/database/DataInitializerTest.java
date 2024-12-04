package com.example.dynamicpricing.infrastructure.database;

import com.example.dynamicpricing.infrastructure.repository.MongoPriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private MongoPriceRepository mongoPriceRepository;

    @Test
    void givenValidJson_whenRun_thenSaveAllPrices() {
        String validJson = "[{\"product_id\": 1, \"brand_id\": 1, \"start_date\": \"2024-12-01T10:00:00Z\", "
                + "\"end_date\": \"2024-12-31T10:00:00Z\"}]";
        InputStream inputStream = new ByteArrayInputStream(validJson.getBytes(StandardCharsets.UTF_8));

        Supplier<InputStream> inputStreamSupplier = () -> inputStream;

        DataInitializer dataInitializer = new DataInitializer(mongoPriceRepository, inputStreamSupplier);

        doNothing().when(mongoPriceRepository).deleteAll();
        when(mongoPriceRepository.saveAll(any(Iterable.class))).thenReturn(Collections.emptyList());

        dataInitializer.run();

        verify(mongoPriceRepository).deleteAll();
        verify(mongoPriceRepository).saveAll(anyList());
    }
}
