package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import org.springframework.stereotype.Component;

@Component
public class PriceResponseMapperImpl implements PriceResponseMapper {

    @Override
    public PriceResponse toPriceResponse(PriceResponseDto priceResponseDto) {
        return PriceResponse.builder()
                .productId(priceResponseDto.productId())
                .brandId(priceResponseDto.brandId())
                .priceList(priceResponseDto.priceList())
                .startDate(priceResponseDto.startDate())
                .endDate(priceResponseDto.endDate())
                .price(priceResponseDto.price())
                .currency(priceResponseDto.currency())
                .build();
    }
}
