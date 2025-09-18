package com.loopers.domain.ranking;

import java.util.List;

public interface RankingBuffer {
    void recordWeekly(WeeklyRankingMetric metric);

    Integer getWeeklyRank(Long productId);

    void record(MonthlyRankingScore score);

    List<RankingBoardInfo> getMonthlyRankings(int limit);

    void clearMonthlyBuffer();
}
