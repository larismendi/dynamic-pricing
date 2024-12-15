package com.example.dynamicpricing.application.usecase;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.application.exception.PriceNotAvailableException;
import com.example.dynamicpricing.application.mapper.PriceUseCaseMapper;
import com.example.dynamicpricing.domain.exception.PriceNotFoundException;
import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.service.PriceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DeterminePriceUseCaseImplTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final ZonedDateTime START_DATE = ZonedDateTime.of(2020, 6, 14, 0, 0, 0, 0, ZoneId.of("UTC"));
    private static final ZonedDateTime END_DATE = ZonedDateTime.of(2020, 6, 14, 23, 59, 59, 0, ZoneId.of("UTC"));
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 2;
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);

    @Mock
    private PriceService priceService;

    @Mock
    private PriceUseCaseMapper priceUseCaseMapper;

    @InjectMocks
    private DeterminePriceUseCaseImpl determinePriceUseCase;

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
    void givenValidPriceDto_whenDeterminatePrice_thenReturnPriceResponse() {
        final PriceDto priceDto = new PriceDto(PRODUCT_ID, BRAND_ID, ZonedDateTime.now());
        final Price price = Price.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priority(PRIORITY)
                .priceList(PRICE_LIST)
                .price(PRICE)
                .currency(CURRENCY)
                .build();
        final PriceResponseDto expectedPriceResponse = new PriceResponseDto(
                PRODUCT_ID,
                BRAND_ID,
                PRICE_LIST,
                START_DATE.toString(),
                END_DATE.toString(),
                PRICE.doubleValue(),
                CURRENCY);

        when(priceService.getApplicablePrice(priceDto.brandId(), priceDto.productId(), priceDto.applicationDate()))
                .thenReturn(price);
        when(priceUseCaseMapper.toResponseDto(price)).thenReturn(expectedPriceResponse);

        final PriceResponseDto actualPriceResponse = determinePriceUseCase.determinatePrice(priceDto);

        assertNotNull(actualPriceResponse);
        assertEquals(expectedPriceResponse.productId(), actualPriceResponse.productId());
        assertEquals(expectedPriceResponse.price(), actualPriceResponse.price(), 0.0);
        assertEquals(expectedPriceResponse.currency(), actualPriceResponse.currency());
    }

    @Test
    void givenNoPriceFound_whenDeterminatePrice_thenThrowPriceNotAvailableException() {
        final PriceDto priceDto = new PriceDto(PRODUCT_ID, BRAND_ID, ZonedDateTime.now());

        when(priceService.getApplicablePrice(any(int.class), any(int.class), any(ZonedDateTime.class)))
                .thenThrow(new PriceNotFoundException("Price not found"));

        assertThrows(PriceNotAvailableException.class, () -> determinePriceUseCase.determinatePrice(priceDto));
    }
}
