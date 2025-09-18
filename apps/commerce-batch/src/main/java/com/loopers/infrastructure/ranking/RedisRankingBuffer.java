package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.MonthlyRankingScore;
import com.loopers.domain.ranking.RankingBuffer;
import com.loopers.domain.ranking.RankingBoardInfo;
import com.loopers.domain.ranking.WeeklyRankingMetric;
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
    private final RedisTemplate<String, String> materRedisTemplate;
    private final RedisTemplate<String, String> defaultRedisTemplate;

    @Override
    public void recordWeekly(WeeklyRankingMetric metric) {
        materRedisTemplate.opsForZSet().add("weekly_ranking", metric.productId().toString(), metric.score());
    }

    @Override
    public Integer getWeeklyRank(Long productId) {
        Long rank = defaultRedisTemplate.opsForZSet().reverseRank("weekly_ranking", productId.toString());
        return rank == null ? null : rank.intValue() + 1;
    }

    @Override
    public void record(MonthlyRankingScore score) {
        materRedisTemplate.opsForZSet().add("monthly_ranking", score.productId().toString(), score.score());
    }

    @Override
    public List<RankingBoardInfo> getMonthlyRankings(int limit) {
        Set<TypedTuple<String>> monthlyRanking = defaultRedisTemplate.opsForZSet()
                .reverseRangeWithScores("monthly_ranking", 0, limit - 1);

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
        materRedisTemplate.delete("monthly_ranking");
    }
}
