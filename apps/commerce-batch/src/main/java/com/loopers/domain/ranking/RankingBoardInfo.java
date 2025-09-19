package com.loopers.domain.ranking;

public record RankingBoardInfo(
        Long productId,
        Double score,
        Double weightedScore,
        Integer rank
) {
}
