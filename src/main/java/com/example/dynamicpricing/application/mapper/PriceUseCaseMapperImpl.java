package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import org.springframework.stereotype.Component;

@Component
public class PriceUseCaseMapperImpl implements PriceUseCaseMapper {

    @Override
    public PriceResponse toResponse(Price price) {
        return PriceResponse.builder()
                .productId(price.getProductId())
                .brandId(price.getBrandId())
                .priceList(price.getPriceList())
                .startDate(price.getStartDate().toString())
                .endDate(price.getEndDate().toString())
                .price(price.getPrice().doubleValue())
                .currency(price.getCurrency())
                .build();
    }
}
