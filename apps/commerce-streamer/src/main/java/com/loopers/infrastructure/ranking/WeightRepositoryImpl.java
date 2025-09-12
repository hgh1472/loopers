package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.Weight;
import com.loopers.domain.ranking.WeightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WeightRepositoryImpl implements WeightRepository {
    private final WeightJpaRepository weightJpaRepository;

    @Override
    public Weight findActivateWeight() {
        return weightJpaRepository.findByActivateTrue();
    }

    @Override
    public Weight findLatestWeight() {
        return weightJpaRepository.findFirstByOrderByCreatedAtDesc();
    }
}
