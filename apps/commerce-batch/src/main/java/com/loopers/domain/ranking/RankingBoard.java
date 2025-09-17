package com.loopers.domain.ranking;

public interface RankingBoard {
    void recordWeekly(WeeklyRankingMetric metric);

    Integer getWeeklyRank(Long productId);

}
