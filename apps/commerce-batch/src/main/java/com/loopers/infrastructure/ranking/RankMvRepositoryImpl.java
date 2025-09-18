package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyProductRankMv;
import com.loopers.domain.ranking.RankMvRepository;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RankMvRepositoryImpl implements RankMvRepository {
    private final WeeklyProductRankMvJpaRepository weeklyJpaRepository;
    private final MonthlyProductRankMvJpaRepository monthlyJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void saveWeeklyRankingMvs(Iterable<WeeklyProductRankMv> entities) {
        String sql = "INSERT INTO mv_product_rank_weekly (ref_product_id, weekly_rank, score, date) VALUES (?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (WeeklyProductRankMv entity : entities) {
            batchArgs.add(new Object[]{
                    entity.getProductId(),
                    entity.getRank(),
                    entity.getScore(),
                    entity.getDate()
            });
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public void saveMonthlyRankingMvs(Iterable<MonthlyProductRankMv> entities) {
        String sql = "INSERT INTO mv_product_rank_monthly (ref_product_id, monthly_rank, score, date) VALUES (?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (MonthlyProductRankMv entity : entities) {
            batchArgs.add(new Object[]{
                    entity.getProductId(),
                    entity.getRank(),
                    entity.getScore(),
                    entity.getDate()
            });
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
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
