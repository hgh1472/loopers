package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyProductRankMv;
import com.loopers.domain.ranking.RankMvRepository;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RankMvRepositoryImpl implements RankMvRepository {
    private final WeeklyProductRankMvJpaRepository weeklyJpaRepository;
    private final MonthlyProductRankMvJpaRepository monthlyJpaRepository;

    @Override
    public void saveWeeklyRankingMvs(Iterable<WeeklyProductRankMv> entities) {
        weeklyJpaRepository.saveAll(entities);
    }

    @Override
    public void saveMonthlyRankingMvs(Iterable<MonthlyProductRankMv> entities) {
        monthlyJpaRepository.saveAll(entities);
    }

    @Override
    public List<WeeklyProductRankMv> findWeeklyRankMv(LocalDate date) {
        return weeklyJpaRepository.findWeeklyRankMvs(date);
    }

    @Override
    public List<MonthlyProductRankMv> findMonthlyRankMv(LocalDate date) {
        return monthlyJpaRepository.findByDate(date);
    }

    @Override
    public Optional<MonthlyProductRankMv> findMonthlyRankMvByProductId(LocalDate date, Long productId) {
        return monthlyJpaRepository.findByDateAndProductId(date, productId);
    }
}
