package com.example.dynamicpricing.infrastructure.controller;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.application.usecase.DeterminePriceUseCase;
import com.example.dynamicpricing.infrastructure.controller.request.PriceRequest;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import com.example.dynamicpricing.infrastructure.mapper.PriceRequestMapper;
import com.example.dynamicpricing.infrastructure.mapper.PriceResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PriceController {

    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);
    private static final String PRICE_CALCULATED_SUCCESSFULLY = "Price calculated successfully: {}";
    private static final String REQUEST_TO_CALCULATE_PRICE_WITH_PARAMETERS_PRODUCT_ID_BRAND_ID_APPLICATION_DATE =
            "Received request to calculate price with parameters: productId = {}, brandId = {}, applicationDate = {}";

    private final DeterminePriceUseCase determinePriceUseCase;
    private final PriceRequestMapper priceRequestMapper;
    private final PriceResponseMapper priceResponseMapper;

    public PriceController(DeterminePriceUseCase determinePriceUseCase,
                           PriceRequestMapper priceRequestMapper,
                           PriceResponseMapper priceResponseMapper) {
        this.determinePriceUseCase = determinePriceUseCase;
        this.priceRequestMapper = priceRequestMapper;
        this.priceResponseMapper = priceResponseMapper;
    }

    @Operation(
            summary = "Calculate the price of a product",
            description = "This method calculates the price based on the product and brand ID, along with"
                    + " the application date.",
            parameters = {
                    @Parameter(name = "productId", description = "ID of the product", required = true),
                    @Parameter(name = "brandId", description = "ID of the brand", required = true),
                    @Parameter(name = "applicationDate", description = "The application date", required = true)
            },
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
                                                  "startDate": "2020-06-14T00:00:00Z",
                                                  "endDate": "2020-12-31T23:59:59Z",
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
    @GetMapping("/price")
    public ResponseEntity<PriceResponse> calculatePrice(@Valid PriceRequest priceRequest) {
        logger.info(REQUEST_TO_CALCULATE_PRICE_WITH_PARAMETERS_PRODUCT_ID_BRAND_ID_APPLICATION_DATE,
                priceRequest.getProductId(), priceRequest.getBrandId(), priceRequest.getApplicationDate());

        final PriceDto priceDto = priceRequestMapper.toDto(priceRequest);
        final PriceResponseDto priceResponseDto = determinePriceUseCase.determinatePrice(priceDto);
        final PriceResponse priceResponse = priceResponseMapper.toPriceResponse(priceResponseDto);

        logger.info(PRICE_CALCULATED_SUCCESSFULLY, priceResponse);
        return ResponseEntity.ok(priceResponse);
    }
}
