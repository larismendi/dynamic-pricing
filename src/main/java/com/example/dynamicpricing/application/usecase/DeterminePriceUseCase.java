package com.example.dynamicpricing.application.usecase;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;

public interface DeterminePriceUseCase {
    PriceResponse determinatePrice(PriceDto priceDto);
}
