package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.WeeklyRankingProductMv;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyRankingProductMvJpaRepository extends JpaRepository<WeeklyRankingProductMv, Long> {
    Page<WeeklyRankingProductMv> findByDateOrderByRankAsc(LocalDate date, Pageable pageable);
}
