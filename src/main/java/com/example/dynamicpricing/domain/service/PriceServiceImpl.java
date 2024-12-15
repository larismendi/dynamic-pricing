package com.example.dynamicpricing.domain.service;

import com.example.dynamicpricing.domain.exception.PriceNotFoundException;
import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.repository.PriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {

    private static final String PRICE_IS_EMPTY = "No price found for the given parameters.";
    private static final String NO_APPLICABLE_PRICE_FOUND = "No applicable price found for the given parameters.";
    private static final Logger logger = LoggerFactory.getLogger(PriceServiceImpl.class);
    private static final String PRICE_FOR_BRAND_ID_PRODUCT_ID_APPLICATION_DATE =
            "Getting applicable price for brandID {}, productID {}, applicationDate {}";
    private static final String PRICE_WITH_BRAND_ID_PRODUCT_ID_APPLICATION_DATE_NOT_FOUND =
            "Price with brandID {}, productID {}, applicationDate {} not found";
    private static final String DATA_ERROR = "Data error encountered while fetching prices.";
    private static final String ERROR_FETCHING_PRICES_FROM_THE_DATABASE = "Error fetching prices from the database.";

    private final PriceRepository priceRepository;

    public PriceServiceImpl(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Price getApplicablePrice(int brandId, int productId, ZonedDateTime applicationDate) {
        logger.info(PRICE_FOR_BRAND_ID_PRODUCT_ID_APPLICATION_DATE,
                brandId, productId, applicationDate);
        try {
            final List<Price> prices = priceRepository.findPrices(productId, brandId, applicationDate);

            if (prices.isEmpty()) {
                logger.warn(PRICE_WITH_BRAND_ID_PRODUCT_ID_APPLICATION_DATE_NOT_FOUND,
                        brandId, productId, applicationDate);
                throw new PriceNotFoundException(PRICE_IS_EMPTY);
            }

            return prices.stream()
                    .filter(price -> price.isApplicable(applicationDate))
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.warn(PRICE_WITH_BRAND_ID_PRODUCT_ID_APPLICATION_DATE_NOT_FOUND,
                                brandId, productId, applicationDate);
                        return new PriceNotFoundException(NO_APPLICABLE_PRICE_FOUND);
                    });
        } catch (DataAccessException ex) {
            logger.error(DATA_ERROR, ex);
            throw new PriceNotFoundException(ERROR_FETCHING_PRICES_FROM_THE_DATABASE, ex);
        }
    }
}
