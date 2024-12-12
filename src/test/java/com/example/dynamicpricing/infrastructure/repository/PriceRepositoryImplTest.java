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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PriceRepositoryImplTest {

    private static final int BRAND_ID = 5;
    private static final int PRODUCT_ID = 101;
    private static final ZonedDateTime APPLICATION_DATE = ZonedDateTime.of(2023, 12, 1, 14, 0, 0, 0, ZoneId.of("UTC"));
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "priority"));

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
        final Instant formattedDate = APPLICATION_DATE.toInstant();

        final PriceEntity priceEntity1 = mock(PriceEntity.class);
        final PriceEntity priceEntity2 = mock(PriceEntity.class);

        final Price domainPrice1 = mock(Price.class);
        final Price domainPrice2 = mock(Price.class);

        when(mongoPriceRepository.findTopByProductIdAndBrandIdAndApplicationDate(
                PRODUCT_ID, BRAND_ID, formattedDate, PAGE_REQUEST))
                .thenReturn(Arrays.asList(priceEntity1, priceEntity2));

        when(priceEntityMapper.toDomain(priceEntity1, APPLICATION_DATE.getZone())).thenReturn(domainPrice1);
        when(priceEntityMapper.toDomain(priceEntity2, APPLICATION_DATE.getZone())).thenReturn(domainPrice2);

        final List<Price> result = priceRepositoryImpl.findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertThat(result).containsExactly(domainPrice1, domainPrice2);
        verify(mongoPriceRepository, times(1))
                .findTopByProductIdAndBrandIdAndApplicationDate(
                        PRODUCT_ID, BRAND_ID, formattedDate, PAGE_REQUEST);
        verify(priceEntityMapper, times(1)).toDomain(priceEntity1, APPLICATION_DATE.getZone());
        verify(priceEntityMapper, times(1)).toDomain(priceEntity2, APPLICATION_DATE.getZone());
    }

    @Test
    void givenNoPricesFound_whenFindPrices_thenReturnEmptyList() {
        final Instant formattedDate = APPLICATION_DATE.toInstant();

        when(mongoPriceRepository.findTopByProductIdAndBrandIdAndApplicationDate(
                PRODUCT_ID, BRAND_ID, formattedDate, PAGE_REQUEST))
                .thenReturn(Collections.emptyList());

        final List<Price> result = priceRepositoryImpl.findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);

        assertThat(result).isEmpty();
        verify(mongoPriceRepository, times(1))
                .findTopByProductIdAndBrandIdAndApplicationDate(
                        PRODUCT_ID, BRAND_ID, formattedDate, PAGE_REQUEST);
        verifyNoInteractions(priceEntityMapper);
    }
}

