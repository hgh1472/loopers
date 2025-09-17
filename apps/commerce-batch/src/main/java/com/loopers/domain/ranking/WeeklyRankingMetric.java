package com.loopers.domain.ranking;

import java.time.LocalDate;

public record WeeklyRankingMetric(
        Long productId,
        Double score
) {
}
