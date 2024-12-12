package com.example.dynamicpricing.domain.service;

import com.example.dynamicpricing.domain.model.Price;

import java.time.ZonedDateTime;

public interface PriceService {
    Price getApplicablePrice(int brandId, int productId, ZonedDateTime applicationDate);
}
