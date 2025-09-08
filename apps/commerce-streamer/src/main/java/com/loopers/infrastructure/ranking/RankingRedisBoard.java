package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingBoard;
import com.loopers.key.MetricsKeys;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingRedisBoard implements RankingBoard {
    private final RedisTemplate<String, String> masterRedisTemplate;

    @Override
    public void recordLikeCount(Long productId, Double score, LocalDate date) {
        String key = MetricsKeys.PRODUCT_SCORE.getKey(date);
        masterRedisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), score);
    }

    @Override
    public void recordViewCount(Long productId, Double score, LocalDate date) {
        String key = MetricsKeys.PRODUCT_SCORE.getKey(date);
        masterRedisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), score);
    }

    @Override
    public void recordSalesCount(Long productId, Double score, LocalDate date) {
        String key = MetricsKeys.PRODUCT_SCORE.getKey(date);
        masterRedisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), score);
    }

    @Override
    public void carryOverScores(LocalDate date) {
        String todayKey = MetricsKeys.PRODUCT_SCORE.getKey(date);

        String tomorrowKey = MetricsKeys.PRODUCT_SCORE.getKey(date.plusDays(1));
        masterRedisTemplate.opsForZSet()
                .unionAndStore(todayKey, Collections.emptyList(), tomorrowKey, Aggregate.SUM, Weights.of(0.1));

        masterRedisTemplate.expire(todayKey, Duration.ofDays(1));
        masterRedisTemplate.expire(tomorrowKey, Duration.ofDays(2));
    }

    @Override
    public List<Long> getTopRankedProducts(LocalDate date, int topN) {
        String key = MetricsKeys.PRODUCT_SCORE.getKey(date);
        return masterRedisTemplate.opsForZSet()
                .reverseRange(key, 0, topN - 1)
                .stream()
                .map(Long::valueOf)
                .toList();
    }

}
