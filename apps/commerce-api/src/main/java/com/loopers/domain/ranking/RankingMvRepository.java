package com.loopers.domain.ranking;

import java.time.LocalDate;
import org.springframework.data.domain.Page;

public interface RankingMvRepository {
    Page<WeeklyRankingProductMv> findWeeklyRankingProducts(int page, int size, LocalDate date);

    WeeklyRankingProductMv save(WeeklyRankingProductMv mv);
}
