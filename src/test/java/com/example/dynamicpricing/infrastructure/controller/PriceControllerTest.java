package com.example.dynamicpricing.infrastructure.controller;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.application.usecase.DeterminePriceUseCase;
import com.example.dynamicpricing.infrastructure.controller.request.PriceRequest;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import com.example.dynamicpricing.infrastructure.mapper.PriceRequestMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceControllerTest {

    private static final int BRAND_ID = 5;
    private static final int PRODUCT_ID = 101;
    private static final int PRICE_LIST = 1;
    private static final BigDecimal PRICE = BigDecimal.valueOf(99.99);
    private static final String CURRENCY = "EUR";
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final ZonedDateTime APPLICATION_DATE = ZonedDateTime.of(2023, 12, 1, 14, 0, 0, 0, ZONE_ID);
    private static final ZonedDateTime START_DATE = ZonedDateTime.of(2023, 11, 1, 0, 0, 0, 0, ZONE_ID);
    private static final ZonedDateTime END_DATE = ZonedDateTime.of(2023, 12, 1, 23, 59, 59, 0, ZONE_ID);
    private static final String PRICE_DETERMINATION_FAIL_MESSAGE = "Price determination failed";

    @Mock
    private DeterminePriceUseCase determinePriceUseCase;

    @Mock
    private PriceRequestMapper priceRequestMapper;

    @InjectMocks
    private PriceController priceController;

    private PriceRequest priceRequest;
    private PriceDto priceDto;

    void getPriceRequest() {
        priceRequest = PriceRequest.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .applicationDate(APPLICATION_DATE)
                .build();
    }

    void getPriceDto() {
        priceDto = new PriceDto(PRODUCT_ID, BRAND_ID, APPLICATION_DATE);
    }

    @Test
    void givenValidPriceRequest_whenCalculatePrice_thenReturnsPriceResponse() {
        getPriceRequest();
        getPriceDto();
        final PriceResponse expectedResponse = PriceResponse.builder()
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(PRICE_LIST)
                .price(PRICE.doubleValue())
                .startDate(START_DATE.toString())
                .endDate(END_DATE.toString())
                .currency(CURRENCY)
                .build();

        when(priceRequestMapper.toDto(priceRequest)).thenReturn(priceDto);
        when(determinePriceUseCase.determinatePrice(priceDto)).thenReturn(expectedResponse);

        final ResponseEntity<PriceResponse> responseEntity = priceController.calculatePrice(priceRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());

        verify(priceRequestMapper).toDto(priceRequest);
        verify(determinePriceUseCase).determinatePrice(priceDto);
    }

    @Test
    void givenDeterminePriceUseCaseThrowsException_whenCalculatePrice_thenHandlesException() {
        getPriceRequest();
        getPriceDto();

        when(priceRequestMapper.toDto(priceRequest)).thenReturn(priceDto);
        when(determinePriceUseCase.determinatePrice(priceDto))
                .thenThrow(new RuntimeException(PRICE_DETERMINATION_FAIL_MESSAGE));

        final RuntimeException exception = assertThrows(RuntimeException.class,
                () -> priceController.calculatePrice(priceRequest));

        assertEquals(PRICE_DETERMINATION_FAIL_MESSAGE, exception.getMessage());

        verify(priceRequestMapper).toDto(priceRequest);
        verify(determinePriceUseCase).determinatePrice(priceDto);
    }
}
