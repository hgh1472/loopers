package com.loopers.domain.ranking;

import java.time.LocalDate;

public interface RankingScoreRecorder {
    void recordLikeCount(Long productId, Double score, LocalDate date);

    void recordViewCount(Long productId, Double score, LocalDate date);

    void recordSalesCount(Long productId, Double score, LocalDate date);
}
