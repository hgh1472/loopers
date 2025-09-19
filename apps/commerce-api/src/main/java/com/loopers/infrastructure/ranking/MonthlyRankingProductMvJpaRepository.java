package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyRankingProductMv;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyRankingProductMvJpaRepository extends JpaRepository<MonthlyRankingProductMv, Long> {
    Page<MonthlyRankingProductMv> findByDateOrderByRankAsc(LocalDate date, Pageable pageable);
}
