package com.example.dynamicpricing.infrastructure.repository;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.repository.PriceRepository;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.mapper.PriceEntityMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PriceRepositoryImpl implements PriceRepository {

    private final MongoPriceRepository mongoPriceRepository;
    private final PriceEntityMapper priceEntityMapper;

    public PriceRepositoryImpl(MongoPriceRepository mongoPriceRepository,
                               PriceEntityMapper priceEntityMapper) {
        this.mongoPriceRepository = mongoPriceRepository;
        this.priceEntityMapper = priceEntityMapper;
    }

    @Override
    public List<Price> findPrices(int productId, int brandId, LocalDateTime applicationDate) {
        final Instant formatApplicationDate = applicationDate.atZone(ZoneId.of("UTC")).toInstant();
        final List<PriceEntity> priceEntities =
                mongoPriceRepository.findByProductIdAndBrandIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        productId, brandId, formatApplicationDate, formatApplicationDate);

        return priceEntities.stream()
                .map(priceEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
