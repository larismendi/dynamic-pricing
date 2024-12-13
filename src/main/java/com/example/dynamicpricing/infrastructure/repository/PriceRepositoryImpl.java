package com.example.dynamicpricing.infrastructure.repository;

import com.example.dynamicpricing.domain.model.Price;
import com.example.dynamicpricing.domain.repository.PriceRepository;
import com.example.dynamicpricing.infrastructure.entity.PriceEntity;
import com.example.dynamicpricing.infrastructure.mapper.PriceEntityMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZonedDateTime;
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
    public List<Price> findPrices(int productId, int brandId, ZonedDateTime applicationDate) {
        final Instant applicationInstant = applicationDate.toInstant();
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "priority"));

        final List<PriceEntity> priceEntities =
                mongoPriceRepository.findTopByProductIdAndBrandIdAndApplicationDate(
                        productId, brandId, applicationInstant, pageable);

        return priceEntities.stream()
                .map(entity -> priceEntityMapper.toDomain(entity, applicationDate.getZone()))
                .collect(Collectors.toList());
    }
}
