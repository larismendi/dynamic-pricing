package com.example.dynamicpricing.domain.service;

import com.example.dynamicpricing.domain.exception.PriceNotFoundException;
import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceServiceImplTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 35455;
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 1;
    private static final String CURRENCY = "EUR";
    private static final String PRICE_IS_EMPTY = "No price found for the given parameters.";
    private static final String NO_APPLICABLE_PRICE_FOUND = "No applicable price found for the given parameters.";
    private static final ZonedDateTime START_DATE = ZonedDateTime.of(2020, 6, 14, 0, 0, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime END_DATE = ZonedDateTime.of(2020, 6, 14, 23, 59, 59, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime APPLICATION_DATE = ZonedDateTime.of(2020, 6, 14, 15, 0, 0, 0, ZoneId.of("UTC"));
    private static final String DATABASE_ERROR = "Database error";
    private static final String ERROR_FETCHING_PRICES_FROM_THE_DATABASE = "Error fetching prices from the database.";

    private static final Logger logger = LoggerFactory.getLogger(PriceServiceImplTest.class);

    private PriceServiceImpl priceService;

    @Mock
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        try (AutoCloseable ignored = MockitoAnnotations.openMocks(this)) {
            priceService = new PriceServiceImpl(priceRepository);
        } catch (Exception e) {
            logger.error("Error initializing mocks: ", e);
        }
    }

    private Price createPrice(ZonedDateTime startDate,
                              ZonedDateTime endDate,
                              BigDecimal price) {
        return Price.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .startDate(startDate)
                .endDate(endDate)
                .priceList(PRICE_LIST)
                .priority(PRIORITY)
                .price(price)
                .currency(CURRENCY)
                .build();
    }

    private void mockPriceRepository(List<Price> prices) {
        when(priceRepository.findPrices(anyInt(), anyInt(), any(ZonedDateTime.class))).thenReturn(prices);
    }

    @Test
    void givenPriceFound_whenGetApplicablePrice_thenReturnPrice() {
        final Price price = createPrice(START_DATE, END_DATE, BigDecimal.valueOf(35.50));

        mockPriceRepository(List.of(price));

        final Price result = priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        assertNotNull(result);
        assertEquals(price, result);
        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenNoPrices_whenGetApplicablePrice_thenThrowPriceNotFoundException() {
        mockPriceRepository(Collections.emptyList());

        final PriceNotFoundException exception = assertThrows(PriceNotFoundException.class, () ->
                priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE));

        assertEquals(PRICE_IS_EMPTY, exception.getMessage());
        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenPriceIsApplicable_whenGetApplicablePrice_thenReturnPrice() {
        final Price price = createPrice(START_DATE, END_DATE, BigDecimal.valueOf(35.50));

        mockPriceRepository(List.of(price));

        final Price result = priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        assertNotNull(result);
        assertEquals(price, result);
        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenNoPricesAvailable_whenGetApplicablePrice_thenThrowPriceNotFoundException() {
        final ZonedDateTime applicationDate = ZonedDateTime.now();
        when(priceRepository.findPrices(PRODUCT_ID, BRAND_ID, applicationDate)).thenReturn(Collections.emptyList());

        final PriceNotFoundException exception = assertThrows(PriceNotFoundException.class,
                () -> priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, applicationDate));
        assertEquals(PRICE_IS_EMPTY, exception.getMessage());

        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, applicationDate);
    }

    @Test
    void givenPricesWithNoMatchingDate_whenGetApplicablePrice_thenThrowPriceNotFoundException() {
        final ZonedDateTime startDate = ZonedDateTime.of(2024, 11, 29, 10, 0, 0, 0, ZoneId.of("UTC"));
        final ZonedDateTime endDate = ZonedDateTime.of(2024, 11, 29, 18, 0, 0, 0, ZoneId.of("UTC"));
        final List<Price> prices = Collections.singletonList(
                createPrice(startDate, endDate, BigDecimal.valueOf(25.45))
        );
        when(priceRepository.findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE)).thenReturn(prices);

        final PriceNotFoundException exception = assertThrows(PriceNotFoundException.class,
                () -> priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE));
        assertEquals(NO_APPLICABLE_PRICE_FOUND, exception.getMessage());

        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenDataAccessException_whenGetApplicablePrice_thenThrowPriceNotFoundException() {
        when(priceRepository.findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE))
                .thenThrow(new DataAccessException(DATABASE_ERROR) {
                });

        final PriceNotFoundException exception = assertThrows(
                PriceNotFoundException.class,
                () -> priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE)
        );

        assertEquals(ERROR_FETCHING_PRICES_FROM_THE_DATABASE, exception.getMessage());
        assertInstanceOf(DataAccessException.class, exception.getCause());
    }
}
