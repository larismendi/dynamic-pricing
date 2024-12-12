package com.example.dynamicpricing.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Builder
public class Price {
    private int brandId;
    private int productId;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int priceList;
    private int priority;
    private BigDecimal price;
    private String currency;

    public boolean isApplicable(ZonedDateTime applicationDate) {
        return (applicationDate.isEqual(startDate) || applicationDate.isAfter(startDate))
                && (applicationDate.isEqual(endDate) || applicationDate.isBefore(endDate));
    }
}
