package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.DailyRanking;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyRankingJpaRepository extends JpaRepository<DailyRanking, Long> {
    List<DailyRanking> findByDate(LocalDate date);
}
