package com.example.dynamicpricing.infrastructure.repository;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.mapper.PriceEntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PriceRepositoryImplTest {

    private static final int BRAND_ID = 5;
    private static final int PRODUCT_ID = 101;
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2023, 12, 1, 14, 0, 0);
    private static final String ZONE_ID = "UTC";

    @Mock
    private MongoPriceRepository mongoPriceRepository;

    @Mock
    private PriceEntityMapper priceEntityMapper;

    @InjectMocks
    private PriceRepositoryImpl priceRepositoryImpl;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (closeable != null) {
            closeable.close();
        }
    }

    @Test
    void givenValidInput_whenFindPrices_thenReturnPriceList() {
        final Instant formattedDate = APPLICATION_DATE.atZone(ZoneId.of(ZONE_ID)).toInstant();

        final PriceEntity priceEntity1 = mock(PriceEntity.class);
        final PriceEntity priceEntity2 = mock(PriceEntity.class);

        final Price domainPrice1 = mock(Price.class);
        final Price domainPrice2 = mock(Price.class);

        when(mongoPriceRepository.findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                PRODUCT_ID, BRAND_ID, formattedDate, formattedDate))
                .thenReturn(Arrays.asList(priceEntity1, priceEntity2));

        when(priceEntityMapper.toDomain(priceEntity1)).thenReturn(domainPrice1);
        when(priceEntityMapper.toDomain(priceEntity2)).thenReturn(domainPrice2);

        final List<Price> result = priceRepositoryImpl.findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertThat(result).containsExactly(domainPrice1, domainPrice2);
        verify(mongoPriceRepository, times(1))
                .findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        PRODUCT_ID, BRAND_ID, formattedDate, formattedDate);
        verify(priceEntityMapper, times(1)).toDomain(priceEntity1);
        verify(priceEntityMapper, times(1)).toDomain(priceEntity2);
    }

    @Test
    void givenNoPricesFound_whenFindPrices_thenReturnEmptyList() {
        final Instant formattedDate = APPLICATION_DATE.atZone(ZoneId.of(ZONE_ID)).toInstant();

        when(mongoPriceRepository.findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                PRODUCT_ID, BRAND_ID, formattedDate, formattedDate))
                .thenReturn(Collections.emptyList());

        final List<Price> result = priceRepositoryImpl.findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertThat(result).isEmpty();
        verify(mongoPriceRepository, times(1))
                .findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        PRODUCT_ID, BRAND_ID, formattedDate, formattedDate);
        verifyNoInteractions(priceEntityMapper);
    }
}

