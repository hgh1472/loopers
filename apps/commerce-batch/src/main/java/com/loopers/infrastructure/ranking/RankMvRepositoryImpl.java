package com.loopers.infrastructure.ranking;

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
    private final WeeklyProductRankMvJpaRepository jpaRepository;

    @Override
    public void saveAll(Iterable<WeeklyProductRankMv> entities) {
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<WeeklyProductRankMv> findWeeklyRankMv(LocalDate date) {
        return jpaRepository.findWeeklyRankMvs(date);
    }

    @Override
    public Optional<WeeklyProductRankMv> findByProductAndDate(Long productId, LocalDate date) {
        return jpaRepository.findByProductIdAndDate(productId, date);
    }
}
