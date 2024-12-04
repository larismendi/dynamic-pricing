package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.infrastructure.controller.request.PriceRequest;
import org.springframework.stereotype.Component;

@Component
public class PriceRequestMapperImpl implements PriceRequestMapper {

    @Override
    public PriceDto toDto(PriceRequest request) {
        return new PriceDto(
                request.getProductId(),
                request.getBrandId(),
                request.getApplicationDate()
        );
    }
}
