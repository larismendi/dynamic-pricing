package com.example.dynamicpricing.application.usecase;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.application.dto.PriceResponseDto;

public interface DeterminePriceUseCase {
    PriceResponseDto determinatePrice(PriceDto priceDto);
}
