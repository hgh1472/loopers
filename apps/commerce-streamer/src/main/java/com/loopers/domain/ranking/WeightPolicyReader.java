package com.loopers.domain.ranking;

public interface WeightPolicyReader {
    Double getLikeWeight();

    Double getViewWeight();

    Double getSalesWeight();
}
