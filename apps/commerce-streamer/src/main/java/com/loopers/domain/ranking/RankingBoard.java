package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;

public interface RankingBoard {
    void recordLikeCount(Long productId, Double score, LocalDate date);

    void recordViewCount(Long productId, Double score, LocalDate date);

    void recordSalesCount(Long productId, Double score, LocalDate date);

    void carryOverScores(LocalDate date);

    void updateWeights(Double likeWeight, Double viewWeight, Double salesWeight, LocalDate date);

    List<Long> getTopRankedProducts(LocalDate date, int topN);
}
