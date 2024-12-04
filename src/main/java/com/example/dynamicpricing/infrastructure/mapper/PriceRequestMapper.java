package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.infrastructure.controller.request.PriceRequest;

public interface PriceRequestMapper {
    PriceDto toDto(PriceRequest request);
}
