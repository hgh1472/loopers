package com.loopers.domain.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingWeightCalculator {
    private final WeightPolicyReader weightPolicyReader;

    public Double calculateLikeScore(Long count) {
        return count * weightPolicyReader.getLikeWeight();
    }

    public Double calculateViewScore(Long count) {
        return count * weightPolicyReader.getViewWeight();
    }

    public Double calculateSalesScore(Long count) {
        return count * weightPolicyReader.getSalesWeight();
    }
}
