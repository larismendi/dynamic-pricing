package com.example.dynamicpricing.infrastructure.database;

import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    private static final int ONE_TIME = 1;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private Supplier<InputStream> inputStreamSupplier;

    @Mock
    private IndexOperations indexOperations;

    private DataInitializer dataInitializer;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        dataInitializer = new DataInitializer(mongoTemplate, inputStreamSupplier);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void givenValidJson_whenRun_thenSaveAllPrices() throws IOException {
        final String validJson = "[{\"product_id\": 1, \"brand_id\": 1, \"start_date\": \"2024-12-01T10:00:00Z\", "
                + "\"end_date\": \"2024-12-31T10:00:00Z\"}]";
        final InputStream inputStream = new ByteArrayInputStream(validJson.getBytes(StandardCharsets.UTF_8));

        inputStreamSupplier = () -> inputStream;

        dataInitializer = new DataInitializer(mongoTemplate, inputStreamSupplier);

        doNothing().when(mongoTemplate).dropCollection(PriceEntity.class);
        when(mongoTemplate.insertAll(anyList())).thenReturn(Collections.emptyList());
        when(mongoTemplate.indexOps(PriceEntity.class)).thenReturn(indexOperations);
        when(indexOperations.ensureIndex(any(Index.class))).thenAnswer(invocation -> null);

        dataInitializer.run();

        verify(mongoTemplate, times(ONE_TIME)).dropCollection(PriceEntity.class);
        verify(mongoTemplate, times(ONE_TIME)).insertAll(anyList());
        verify(mongoTemplate, times(ONE_TIME)).indexOps(PriceEntity.class);
        verify(indexOperations, times(ONE_TIME)).ensureIndex(any(Index.class));
    }

    @Test
    void givenIOException_whenRun_thenHandleExceptionAndLogError() {
        final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        doAnswer(invocation -> {
            throw new IOException("Simulated IOException");
        }).when(inputStreamSupplier).get();

        assertThrows(IOException.class, () -> dataInitializer.run());
        verify(mongoTemplate, never()).dropCollection(PriceEntity.class);
        verify(mongoTemplate, never()).insertAll(anyList());
        verify(mongoTemplate, never()).indexOps(PriceEntity.class);
    }
}
