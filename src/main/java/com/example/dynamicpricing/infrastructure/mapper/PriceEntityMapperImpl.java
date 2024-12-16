package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneId;

@Component
public class PriceEntityMapperImpl implements PriceEntityMapper {

    @Override
    public Price toDomain(PriceEntity entity, ZoneId zoneId) {
        return Price.builder()
                .productId(entity.getProductId())
                .brandId(entity.getBrandId())
                .priceList(entity.getPriceList())
                .startDate(entity.getStartDate().atZone(zoneId))
                .endDate(entity.getEndDate().atZone(zoneId))
                .price(BigDecimal.valueOf(entity.getPrice()))
                .currency(entity.getCurr())
                .build();
    }
}
