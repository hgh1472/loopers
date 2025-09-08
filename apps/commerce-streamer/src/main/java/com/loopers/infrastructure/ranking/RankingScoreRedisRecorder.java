package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingScoreRecorder;
import com.loopers.key.MetricsKeys;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingScoreRedisRecorder implements RankingScoreRecorder {
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
}
