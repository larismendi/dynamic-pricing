package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;

public interface PriceResponseMapper {
    PriceResponse toPriceResponse(PriceResponseDto priceResponseDto);
}
