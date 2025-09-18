package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.WeeklyProductRankMv;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WeeklyProductRankMvJpaRepository extends JpaRepository<WeeklyProductRankMv, Long> {
    @Query("SELECT w FROM WeeklyProductRankMv w WHERE w.date = :date ORDER BY w.rank ASC")
    List<WeeklyProductRankMv> findWeeklyRankMvs(LocalDate date);

    Optional<WeeklyProductRankMv> findByProductIdAndDate(Long productId, LocalDate date);
}
