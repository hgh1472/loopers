package com.loopers.domain.ranking;

import java.time.LocalDate;

public interface WeightPolicyReader {
    Double getLikeWeight(LocalDate date);

    Double getViewWeight(LocalDate date);

    Double getSalesWeight(LocalDate date);
}
