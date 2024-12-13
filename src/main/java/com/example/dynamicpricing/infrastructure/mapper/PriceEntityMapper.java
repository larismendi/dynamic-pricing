package com.example.dynamicpricing.infrastructure.mapper;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;

import java.time.ZoneId;

public interface PriceEntityMapper {
    Price toDomain(PriceEntity entity, ZoneId zoneId);
}
