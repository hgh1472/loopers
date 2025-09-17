package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RankMvRepository {
    void saveAll(Iterable<WeeklyProductRankMv> entities);

    List<WeeklyProductRankMv> findWeeklyRankMv(LocalDate date);

    Optional<WeeklyProductRankMv> findByProductAndDate(Long productId, LocalDate date);
}
