package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceEntityMapperImpl implements PriceEntityMapper {

    @Override
    public Price toDomain(PriceEntity entity) {
        return Price.builder()
                .productId(entity.getProductId())
                .brandId(entity.getBrandId())
                .priceList(entity.getPriceList())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .price(BigDecimal.valueOf(entity.getPrice()))
                .currency(entity.getCurr())
                .priority(entity.getPriority())
                .build();
    }
}
