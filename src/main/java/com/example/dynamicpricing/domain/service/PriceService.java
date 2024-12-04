package com.example.dynamicpricing.domain.service;

import com.example.dynamicpricing.domain.model.Price;

import java.time.LocalDateTime;

public interface PriceService {
    Price getApplicablePrice(int brandId, int productId, LocalDateTime applicationDate);
}
