package com.example.dynamicpricing.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class Price {
    private int brandId;
    private int productId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int priceList;
    private int priority;
    private BigDecimal price;
    private String currency;

    public boolean isApplicable(LocalDateTime applicationDate) {
        return (applicationDate.isEqual(startDate) || applicationDate.isAfter(startDate))
                && (applicationDate.isEqual(endDate) || applicationDate.isBefore(endDate));
    }
}
