package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RankMvRepository {
    void saveWeeklyRankingMvs(Iterable<WeeklyProductRankMv> entities);

    void saveMonthlyRankingMvs(Iterable<MonthlyProductRankMv> entities);

    List<WeeklyProductRankMv> findWeeklyRankMv(LocalDate date);

    List<MonthlyProductRankMv> findMonthlyRankMv(LocalDate date);

    Optional<MonthlyProductRankMv> findMonthlyRankMvByProductId(LocalDate date, Long productId);
}
