package com.example.dynamicpricing.application.usecase;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.application.exception.PriceNotAvailableException;
import com.example.dynamicpricing.application.mapper.PriceUseCaseMapper;
import com.example.dynamicpricing.domain.exception.PriceNotFoundException;
import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.service.PriceService;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GetPriceUseCaseTest {

    private static final int BRAND_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 6, 14, 0, 0, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 6, 14, 23, 59, 59);
    private static final int PRICE_LIST = 1;
    private static final int PRIORITY = 2;
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE = BigDecimal.valueOf(100);

    @Mock
    private PriceService priceService;

    @Mock
    private PriceUseCaseMapper priceUseCaseMapper;

    @InjectMocks
    private GetPriceUseCase getPriceUseCase;

    @Test
    public void givenValidPriceDto_whenGetPrice_thenReturnPriceResponse() {
        PriceDto priceDto = new PriceDto(PRODUCT_ID, BRAND_ID, LocalDateTime.now());
        Price price = Price.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .startDate(START_DATE)
                .endDate(END_DATE)
                .priority(PRIORITY)
                .priceList(PRICE_LIST)
                .price(PRICE)
                .currency(CURRENCY)
                .build();
        PriceResponse expectedPriceResponse = PriceResponse.builder()
                .brandId(BRAND_ID)
                .productId(PRODUCT_ID)
                .startDate(START_DATE.toString())
                .endDate(END_DATE.toString())
                .priceList(PRICE_LIST)
                .price(PRICE.doubleValue())
                .currency(CURRENCY)
                .build();

        when(priceService.getApplicablePrice(priceDto.brandId(), priceDto.productId(), priceDto.applicationDate()))
                .thenReturn(price);
        when(priceUseCaseMapper.toResponse(price)).thenReturn(expectedPriceResponse);

        PriceResponse actualPriceResponse = getPriceUseCase.getPrice(priceDto);

        assertNotNull(actualPriceResponse);
        assertEquals(expectedPriceResponse.getProductId(), actualPriceResponse.getProductId());
        assertEquals(expectedPriceResponse.getPrice(), actualPriceResponse.getPrice(), 0.0);
        assertEquals(expectedPriceResponse.getCurrency(), actualPriceResponse.getCurrency());
    }

    @Test
    public void givenNoPriceFound_whenGetPrice_thenThrowPriceNotAvailableException() {
        PriceDto priceDto = new PriceDto(PRODUCT_ID, BRAND_ID, LocalDateTime.now());

        when(priceService.getApplicablePrice(priceDto.brandId(), priceDto.productId(), priceDto.applicationDate()))
                .thenThrow(new PriceNotFoundException("Price not found"));

        assertThrows(PriceNotAvailableException.class, () -> getPriceUseCase.getPrice(priceDto));
    }
}
