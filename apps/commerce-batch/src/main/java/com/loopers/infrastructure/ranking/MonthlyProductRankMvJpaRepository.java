package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyProductRankMv;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyProductRankMvJpaRepository extends JpaRepository<MonthlyProductRankMv, Long> {
    List<MonthlyProductRankMv> findByDate(LocalDate date);

    Optional<MonthlyProductRankMv> findByDateAndProductId(LocalDate date, Long productId);
}
