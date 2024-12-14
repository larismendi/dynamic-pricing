package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.domain.model.Price;

public interface PriceUseCaseMapper {
    PriceResponseDto toResponseDto(Price price);
}
