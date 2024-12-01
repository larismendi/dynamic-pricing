package com.example.dynamicpricing.domain.repository;

import com.example.dynamicpricing.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository  {
    List<Price> findPrices(int productId, int brandId, LocalDateTime applicationDate);
}
