package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingBoard;
import com.loopers.domain.ranking.WeeklyRankingMetric;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRankingBoard implements RankingBoard {
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
}
