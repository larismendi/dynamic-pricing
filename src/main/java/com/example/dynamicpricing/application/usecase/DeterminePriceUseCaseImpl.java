package com.example.dynamicpricing.application.usecase;

import com.example.dynamicpricing.application.dto.PriceDto;
import com.example.dynamicpricing.application.dto.PriceResponseDto;
import com.example.dynamicpricing.application.exception.PriceNotAvailableException;
import com.example.dynamicpricing.application.mapper.PriceUseCaseMapper;
import com.example.dynamicpricing.domain.exception.PriceNotFoundException;
import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.service.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeterminePriceUseCaseImpl implements DeterminePriceUseCase {

    private static final String ERROR_IN_DETERMINATE_PRICE_USE_CASE_S = "Error in DeterminatePriceUseCase: %s";
    private static final String FOUND_PRICE_DATA = "Found price data: {}";
    private static final String ERROR_IN_DETERMINATE_PRICE_USE_CASE = "Error in DeterminatePriceUseCase: {}";

    private static final Logger logger = LoggerFactory.getLogger(DeterminePriceUseCaseImpl.class);

    private final PriceService priceService;

    private final PriceUseCaseMapper priceUseCaseMapper;

    public DeterminePriceUseCaseImpl(
            PriceService priceService,
            PriceUseCaseMapper priceUseCaseMapper) {
        this.priceService = priceService;
        this.priceUseCaseMapper = priceUseCaseMapper;
    }

    public PriceResponseDto determinatePrice(PriceDto priceDto) {
        try {
            final Price price = priceService.getApplicablePrice(
                    priceDto.brandId(),
                    priceDto.productId(),
                    priceDto.applicationDate());

            logger.info(FOUND_PRICE_DATA, price);
            return priceUseCaseMapper.toResponseDto(price);
        } catch (PriceNotFoundException ex) {
            logger.warn(ERROR_IN_DETERMINATE_PRICE_USE_CASE, ex.getMessage(), ex);
            throw new PriceNotAvailableException(
                    String.format(ERROR_IN_DETERMINATE_PRICE_USE_CASE_S, ex.getMessage())
            );
        }
    }
}
