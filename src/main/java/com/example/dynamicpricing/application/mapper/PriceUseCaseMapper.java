package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;

public interface PriceUseCaseMapper {
    PriceResponse toResponse(Price price);
}
