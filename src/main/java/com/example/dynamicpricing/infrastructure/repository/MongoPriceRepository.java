package com.example.dynamicpricing.infrastructure.repository;

import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MongoPriceRepository extends MongoRepository<PriceEntity, String> {
    List<PriceEntity> findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            int productId, int brandId, Instant startDate, Instant endDate);
}
