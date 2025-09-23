package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyRankingProductMv;
import com.loopers.domain.ranking.RankingMvRepository;
import com.loopers.domain.ranking.WeeklyRankingProductMv;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RankingMvRepositoryImpl implements RankingMvRepository {
    private final WeeklyRankingProductMvJpaRepository weeklyJpaRepository;
    private final MonthlyRankingProductMvJpaRepository monthlyJpaRepository;

    @Override
    public Page<WeeklyRankingProductMv> findWeeklyRankingProducts(int page, int size, LocalDate date) {
        PageRequest request = PageRequest.of(page - 1, size, Sort.by(Direction.ASC, "rank"));
        return weeklyJpaRepository.findByDateOrderByRankAsc(date, request);
    }

    @Override
    public Page<MonthlyRankingProductMv> findMonthlyRankingProducts(int page, int size, LocalDate date) {
        PageRequest request = PageRequest.of(page - 1, size, Sort.by(Direction.ASC, "rank"));
        return monthlyJpaRepository.findByDateOrderByRankAsc(date, request);
    }

    @Override
    public WeeklyRankingProductMv save(WeeklyRankingProductMv mv) {
        return weeklyJpaRepository.save(mv);
    }

    @Override
    public MonthlyRankingProductMv save(MonthlyRankingProductMv mv) {
        return monthlyJpaRepository.save(mv);
    }
}
