package com.example.dynamicpricing.infrastructure.controller.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceResponse {
    private int productId;
    private int brandId;
    private int priceList;
    private String startDate;
    private String endDate;
    private double price;
    private String currency;
}
