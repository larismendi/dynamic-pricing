package com.example.dynamicpricing.domain.service;

import com.example.dynamicpricing.domain.exception.PriceNotFoundException;
import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.repository.PriceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {
    private static final String PRICE_IS_EMPTY = "No price found for the given parameters.";
    private static final String NO_APPLICABLE_PRICE_FOUND = "No applicable price found for the given parameters.";

    private final PriceRepository priceRepository;

    public PriceServiceImpl(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Price getApplicablePrice(int brandId, int productId, LocalDateTime applicationDate) {
        final List<Price> prices = priceRepository.findPrices(productId, brandId, applicationDate);

        if (prices.isEmpty()) {
            throw new PriceNotFoundException(PRICE_IS_EMPTY);
        }

        return prices.stream()
                .filter(price -> price.isApplicable(applicationDate))
                .max(Comparator.comparingInt(Price::getPriority))
                .orElseThrow(() -> new PriceNotFoundException(NO_APPLICABLE_PRICE_FOUND));
    }
}
