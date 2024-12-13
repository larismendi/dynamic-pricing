package com.example.dynamicpricing.infrastructure.repository;

import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MongoPriceRepository extends MongoRepository<PriceEntity, String> {
    @Query("{ "
            + " 'productId': ?0, "
            + " 'brandId': ?1, "
            + " 'startDate': { '$lte': ?2 }, "
            + " 'endDate': { '$gte': ?2 } "
            + "}")
    List<PriceEntity> findTopByProductIdAndBrandIdAndApplicationDate(
            int productId,
            int brandId,
            Instant applicationDate,
            Pageable pageable);
}
