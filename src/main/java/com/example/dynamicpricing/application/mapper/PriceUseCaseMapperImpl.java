package com.example.dynamicpricing.application.mapper;

import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.domain.model.Price;
import org.springframework.stereotype.Component;

@Component
public class PriceUseCaseMapperImpl implements PriceUseCaseMapper {

    @Override
    public PriceResponseDto toResponseDto(Price price) {
        return new PriceResponseDto(
                price.getProductId(),
                price.getBrandId(),
                price.getPriceList(),
                price.getStartDate().toString(),
                price.getEndDate().toString(),
                price.getPrice().doubleValue(),
                price.getCurrency()
        );
    }
}
