package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;

public interface DailyRankingRepository {
    List<DailyRanking> saveAll(List<DailyRanking> dailyRankings);

    List<DailyRanking> findDailyRankings(LocalDate date);
}
