package com.loopers.domain.ranking;

public interface WeightRepository {
    Weight findActivateWeight();

    Weight findLatestWeight();
}
