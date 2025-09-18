package com.loopers.domain.ranking;

import java.util.List;

public interface RankingBuffer {
    void recordWeekly(WeeklyRankingScore metric);

    List<RankingBoardInfo> getWeeklyRankings(int limit);

    void record(MonthlyRankingScore score);

    List<RankingBoardInfo> getMonthlyRankings(int limit);

    void clearMonthlyBuffer();

    void clearWeeklyBuffer();
}
