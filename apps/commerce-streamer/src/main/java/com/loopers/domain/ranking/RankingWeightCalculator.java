package com.loopers.domain.ranking;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingWeightCalculator {
    private final WeightPolicyReader weightPolicyReader;

    public Double calculateLikeScore(Long count, LocalDate date) {
        return count * weightPolicyReader.getLikeWeight(date);
    }

    public Double calculateViewScore(Long count, LocalDate date) {
        return count * weightPolicyReader.getViewWeight(date);
    }

    public Double calculateSalesScore(Long count, LocalDate date) {
        return count * weightPolicyReader.getSalesWeight(date);
    }
}
