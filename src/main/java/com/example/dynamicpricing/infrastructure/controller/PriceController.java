package com.example.dynamicpricing.infrastructure.controller;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.application.usecase.DeterminePriceUseCase;
import com.example.dynamicpricing.infrastructure.controller.request.PriceRequest;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import com.example.dynamicpricing.infrastructure.mapper.PriceRequestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PriceController {

    private final DeterminePriceUseCase determinePriceUseCase;
    private final PriceRequestMapper priceRequestMapper;

    public PriceController(DeterminePriceUseCase determinePriceUseCase, PriceRequestMapper priceRequestMapper) {
        this.determinePriceUseCase = determinePriceUseCase;
        this.priceRequestMapper = priceRequestMapper;
    }

    @Operation(
            summary = "Calculate the price of a product",
            description = "This method calculates the price based on the product and brand ID, along with"
                    + " the application date.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Price request object with product and brand ID",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PriceRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "productId": 35455,
                                      "brandId": 1,
                                      "applicationDate": "2020-06-14T00:00:00"
                                    }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Price calculated successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PriceResponse.class),
                                    examples = @ExampleObject(value = """
                                                {
                                                  "productId": 35455,
                                                  "brandId": 1,
                                                  "priceList": 1,
                                                  "startDate": "2020-06-14T00:00",
                                                  "endDate": "2020-12-31T23:59:59",
                                                  "price": 35.5,
                                                  "currency": "EUR"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/price")
    public ResponseEntity<PriceResponse> calculatePrice(@Valid @RequestBody PriceRequest priceRequest) {
        final PriceDto priceDto = priceRequestMapper.toDto(priceRequest);
        final PriceResponse priceResponse = determinePriceUseCase.determinatePrice(priceDto);
        return ResponseEntity.ok(priceResponse);
    }
}
