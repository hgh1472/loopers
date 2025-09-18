package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyRankingScore;
import com.loopers.domain.ranking.RankingBuffer;
import com.loopers.domain.ranking.RankingBoardInfo;
import com.loopers.domain.ranking.WeeklyRankingScore;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRankingBuffer implements RankingBuffer {
    public static final String WEEKLY_BUFFER_KEY = "weekly_ranking";
    public static final String MONTHLY_BUFFER_KEY = "monthly_ranking";
    private final RedisTemplate<String, String> materRedisTemplate;
    private final RedisTemplate<String, String> defaultRedisTemplate;

    @Override
    public void recordWeekly(WeeklyRankingScore metric) {
        materRedisTemplate.opsForZSet().incrementScore(WEEKLY_BUFFER_KEY, metric.productId().toString(), metric.score());
    }

    @Override
    public List<RankingBoardInfo> getWeeklyRankings(int limit) {
        Set<TypedTuple<String>> weeklyRanking = defaultRedisTemplate.opsForZSet()
                .reverseRangeWithScores(WEEKLY_BUFFER_KEY, 0, limit - 1);

        int rank = 1;
        List<RankingBoardInfo> rankingBoardInfos = new ArrayList<>();

        for (TypedTuple<String> tuple : weeklyRanking) {
            Long productId = Long.parseLong(tuple.getValue());
            Double score = tuple.getScore();
            rankingBoardInfos.add(new RankingBoardInfo(productId, score, rank++));
        }
        return rankingBoardInfos;
    }

    @Override
    public void record(MonthlyRankingScore score) {
        materRedisTemplate.opsForZSet().incrementScore(MONTHLY_BUFFER_KEY, score.productId().toString(), score.score());
    }

    @Override
    public List<RankingBoardInfo> getMonthlyRankings(int limit) {
        Set<TypedTuple<String>> monthlyRanking = defaultRedisTemplate.opsForZSet()
                .reverseRangeWithScores(MONTHLY_BUFFER_KEY, 0, limit - 1);

        int rank = 1;
        List<RankingBoardInfo> rankingBoardInfos = new ArrayList<>();

        for (TypedTuple<String> tuple : monthlyRanking) {
            Long productId = Long.parseLong(tuple.getValue());
            Double score = tuple.getScore();
            rankingBoardInfos.add(new RankingBoardInfo(productId, score, rank++));
        }
        return rankingBoardInfos;
    }

    @Override
    public void clearMonthlyBuffer() {
        materRedisTemplate.delete(MONTHLY_BUFFER_KEY);
    }

    @Override
    public void clearWeeklyBuffer() {
        materRedisTemplate.delete(WEEKLY_BUFFER_KEY);
    }
}
