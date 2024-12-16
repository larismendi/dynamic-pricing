package com.example.dynamicpricing.infrastructure.repository;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.repository.PriceRepository;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.mapper.PriceEntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PriceRepositoryImpl implements PriceRepository {

    private static final Logger logger = LoggerFactory.getLogger(PriceRepositoryImpl.class);
    private static final String FOUND_PRICE_S_FOR_PRODUCT_ID_BRAND_ID_APPLICATION_DATE =
            "Found {} price(s) for productId: {}, brandId: {}, applicationDate: {}";
    private static final String FETCHING_PRICES_FOR_PRODUCT_ID_BRAND_ID_APPLICATION_DATE =
            "Fetching prices for productId: {}, brandId: {}, applicationDate: {}";
    private static final String ERROR_ACCESSING_DATA_FOR_PRODUCT_ID_BRAND_ID_APPLICATION_DATE =
            "Error accessing data for productId: {}, brandId: {}, applicationDate: {}";

    private final MongoTemplate mongoTemplate;
    private final PriceEntityMapper priceEntityMapper;

    public PriceRepositoryImpl(MongoTemplate mongoTemplate,
                               PriceEntityMapper priceEntityMapper) {
        this.mongoTemplate = mongoTemplate;
        this.priceEntityMapper = priceEntityMapper;
    }

    @Override
    public List<Price> findPrices(int productId, int brandId, ZonedDateTime applicationDate) {
        logger.info(FETCHING_PRICES_FOR_PRODUCT_ID_BRAND_ID_APPLICATION_DATE, productId, brandId, applicationDate);
        try {
            final Instant applicationInstant = applicationDate.toInstant();

            final Query query = new Query()
                    .addCriteria(Criteria.where("productId").is(productId)
                            .and("brandId").is(brandId)
                            .and("startDate").lte(applicationInstant)
                            .and("endDate").gte(applicationInstant))
                    .with(Sort.by(Sort.Direction.DESC, "priority"))
                    .limit(1);

            query.fields()
                    .include("productId")
                    .include("brandId")
                    .include("priceList")
                    .include("startDate")
                    .include("endDate")
                    .include("price")
                    .include("curr");

            final List<PriceEntity> priceEntities = mongoTemplate.find(query, PriceEntity.class);

            logger.info(FOUND_PRICE_S_FOR_PRODUCT_ID_BRAND_ID_APPLICATION_DATE,
                    priceEntities.size(), productId, brandId, applicationDate);
            return priceEntities.stream()
                    .map(entity -> priceEntityMapper.toDomain(entity, applicationDate.getZone()))
                    .collect(Collectors.toList());
        } catch (DataAccessException ex) {
            logger.error(ERROR_ACCESSING_DATA_FOR_PRODUCT_ID_BRAND_ID_APPLICATION_DATE,
                    productId, brandId, applicationDate, ex);
            throw ex;
        }
    }
}
