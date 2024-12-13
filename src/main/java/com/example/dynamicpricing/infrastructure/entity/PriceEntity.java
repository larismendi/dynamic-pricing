package com.example.dynamicpricing.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "prices")
public class PriceEntity {

    @Field("brand_id")
    @JsonProperty("brand_id")
    @NotNull(message = "Brand ID cannot be null")
    private Integer brandId;

    @Field("start_date")
    @JsonProperty("start_date")
    @NotNull(message = "Start date cannot be null")
    private Instant startDate;

    @Field("end_date")
    @JsonProperty("end_date")
    @NotNull(message = "End date cannot be null")
    private Instant endDate;

    @Field("price_list")
    @JsonProperty("price_list")
    @NotNull(message = "Price list cannot be null")
    private Integer priceList;

    @Field("product_id")
    @JsonProperty("product_id")
    @NotNull(message = "Product ID cannot be null")
    private Integer productId;

    @Field("priority")
    @JsonProperty("priority")
    @NotNull(message = "Priority cannot be null")
    private Integer priority;

    @Field("price")
    @JsonProperty("price")
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double price;

    @Field("curr")
    @JsonProperty("curr")
    @NotNull(message = "Currency cannot be null")
    @Size(min = 3, max = 3, message = "Currency must be a valid ISO 4217 code (3 characters)")
    private String curr;

}
