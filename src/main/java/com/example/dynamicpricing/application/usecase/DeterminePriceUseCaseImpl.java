package com.example.dynamicpricing.application.usecase;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.application.exception.PriceNotAvailableException;
import com.example.dynamicpricing.application.mapper.PriceUseCaseMapper;
import com.example.dynamicpricing.domain.exception.PriceNotFoundException;
import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.service.PriceService;
import com.example.dynamicpricing.infrastructure.controller.response.PriceResponse;
import org.springframework.stereotype.Service;

@Service
public class DeterminePriceUseCaseImpl implements DeterminePriceUseCase {

    public static final String ERROR_IN_DETERMINATE_PRICE_USE_CASE_S = "Error in DeterminatePriceUseCase: %s";

    private final PriceService priceService;

    private final PriceUseCaseMapper priceUseCaseMapper;

    public DeterminePriceUseCaseImpl(
            PriceService priceService,
            PriceUseCaseMapper priceUseCaseMapper) {
        this.priceService = priceService;
        this.priceUseCaseMapper = priceUseCaseMapper;
    }

    public PriceResponse determinatePrice(PriceDto priceDto) {
        try {
            final Price price = priceService.getApplicablePrice(
                    priceDto.brandId(),
                    priceDto.productId(),
                    priceDto.applicationDate());

            return priceUseCaseMapper.toResponse(price);
        } catch (PriceNotFoundException ex) {
            throw new PriceNotAvailableException(
                    String.format(ERROR_IN_DETERMINATE_PRICE_USE_CASE_S, ex.getMessage())
            );
        }
    }
}
