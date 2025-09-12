package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.DailyRanking;
import com.loopers.domain.ranking.DailyRankingRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DailyRankingRepositoryImpl implements DailyRankingRepository {
    private final DailyRankingJpaRepository dailyRankingJpaRepository;

    @Override
    public List<DailyRanking> saveAll(List<DailyRanking> dailyRankings) {
        return dailyRankingJpaRepository.saveAll(dailyRankings);
    }

    @Override
    public List<DailyRanking> findDailyRankings(LocalDate date) {
        return dailyRankingJpaRepository.findByDate(date);
    }
}
