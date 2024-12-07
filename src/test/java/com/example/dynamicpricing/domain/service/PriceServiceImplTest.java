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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PriceServiceImplTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 35455;
    private static final String CURRENCY = "EUR";
    private static final String PRICE_IS_EMPTY = "No price found for the given parameters.";
    private static final String NO_APPLICABLE_PRICE_FOUND = "No applicable price found for the given parameters.";
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 6, 14, 23, 59, 59);
    private static final LocalDateTime START_DATE_2 = LocalDateTime.of(2020, 6, 14, 15, 0, 0);
    private static final LocalDateTime END_DATE_2 = LocalDateTime.of(2020, 6, 14, 18, 30, 0);
    private static final LocalDateTime APPLICATION_DATE = LocalDateTime.of(2024, 11, 30, 12, 0, 0);

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

    private Price createPrice(LocalDateTime startDate,
                              LocalDateTime endDate,
                              int priceList,
                              int priority,
                              BigDecimal price) {
        return Price.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .startDate(startDate)
                .endDate(endDate)
                .priceList(priceList)
                .priority(priority)
                .price(price)
                .currency(CURRENCY)
                .build();
    }

    private LocalDateTime getTestApplicationDate() {
        return LocalDateTime.of(2020, 6, 14, 15, 0, 0);
    }

    private void mockPriceRepository(List<Price> prices) {
        when(priceRepository.findPrices(anyInt(), anyInt(), any(LocalDateTime.class))).thenReturn(prices);
    }

    @Test
    void givenPriceFound_whenGetApplicablePrice_thenReturnPrice() {
        LocalDateTime applicationDate = getTestApplicationDate();
        Price price1 = createPrice(START_DATE, END_DATE, 1, 1, BigDecimal.valueOf(35.50));
        Price price2 = createPrice(START_DATE_2, END_DATE_2, 2, 2, BigDecimal.valueOf(25.45));

        mockPriceRepository(List.of(price1, price2));

        Price result = priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, applicationDate);

        assertNotNull(result);
        assertEquals(price2, result);
        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, applicationDate);
    }

    @Test
    void givenNoPrices_whenGetApplicablePrice_thenThrowPriceNotFoundException() {
        LocalDateTime applicationDate = getTestApplicationDate();

        mockPriceRepository(Collections.emptyList());

        PriceNotFoundException exception = assertThrows(PriceNotFoundException.class, () ->
                priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, applicationDate));

        assertEquals(PRICE_IS_EMPTY, exception.getMessage());
        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, applicationDate);
    }

    @Test
    void givenNoApplicablePrice_whenGetApplicablePrice_thenThrowPriceNotFoundException() {
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 10, 0, 0);
        Price price = createPrice(START_DATE, END_DATE, 1, 1, BigDecimal.valueOf(35.50));

        mockPriceRepository(List.of(price));

        PriceNotFoundException exception = assertThrows(PriceNotFoundException.class, () ->
                priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, applicationDate));

        assertEquals(NO_APPLICABLE_PRICE_FOUND, exception.getMessage());
        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, applicationDate);
    }

    @Test
    void givenMultiplePricesWithSamePriority_whenGetApplicablePrice_thenReturnMaxPriority() {
        LocalDateTime applicationDate = getTestApplicationDate();

        Price price1 = createPrice(START_DATE, END_DATE, 1, 1, BigDecimal.valueOf(35.50));
        Price price2 = createPrice(START_DATE_2, END_DATE_2, 2, 1, BigDecimal.valueOf(25.45));

        mockPriceRepository(List.of(price1, price2));

        Price result = priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, applicationDate);

        assertNotNull(result);
        assertEquals(price1, result);
        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, applicationDate);
    }

    @Test
    void givenPriceIsApplicable_whenGetApplicablePrice_thenReturnPrice() {
        LocalDateTime applicationDate = getTestApplicationDate();

        Price price = createPrice(START_DATE, END_DATE, 1, 1, BigDecimal.valueOf(35.50));

        mockPriceRepository(List.of(price));

        Price result = priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, applicationDate);

        assertNotNull(result);
        assertEquals(price, result);
        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, applicationDate);
    }

    @Test
    void givenNoPricesAvailable_whenGetApplicablePrice_thenThrowPriceNotFoundException() {
        LocalDateTime applicationDate = LocalDateTime.now();
        when(priceRepository.findPrices(PRODUCT_ID, BRAND_ID, applicationDate)).thenReturn(Collections.emptyList());

        PriceNotFoundException exception = assertThrows(PriceNotFoundException.class,
                () -> priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, applicationDate));
        assertEquals(PRICE_IS_EMPTY, exception.getMessage());

        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, applicationDate);
    }

    @Test
    void givenPricesWithNoMatchingDate_whenGetApplicablePrice_thenThrowPriceNotFoundException() {
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 29, 10, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 11, 29, 18, 0, 0);
        List<Price> prices = Collections.singletonList(
                createPrice(startDate, endDate, 1, 1, BigDecimal.valueOf(25.45))
        );
        when(priceRepository.findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE)).thenReturn(prices);

        PriceNotFoundException exception = assertThrows(PriceNotFoundException.class,
                () -> priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE));
        assertEquals(NO_APPLICABLE_PRICE_FOUND, exception.getMessage());

        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenMultiplePricesWithDifferentPriorities_whenGetApplicablePrice_thenReturnHighestPriorityPrice() {
        LocalDateTime startDate = LocalDateTime.of(2024, 11, 30, 10, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 11, 30, 20, 0, 0);
        List<Price> prices = Arrays.asList(
                createPrice(startDate, endDate, 1, 0, BigDecimal.valueOf(25.45)),
                createPrice(startDate, endDate, 2, 1, BigDecimal.valueOf(30.50))
        );
        when(priceRepository.findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE)).thenReturn(prices);

        Price result = priceService.getApplicablePrice(BRAND_ID, PRODUCT_ID, APPLICATION_DATE);

        assertNotNull(result);
        assertEquals(2, result.getPriceList());
        assertEquals(BigDecimal.valueOf(30.50), result.getPrice());

        verify(priceRepository, times(1)).findPrices(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }
}
