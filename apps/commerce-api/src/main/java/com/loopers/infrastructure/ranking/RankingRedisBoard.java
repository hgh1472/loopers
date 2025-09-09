package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingBoard;
import com.loopers.key.MetricsKeys;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingRedisBoard implements RankingBoard {
    private final RedisTemplate<String, String> defaultRedisTemplate;

    @Override
    public List<Long> getRankedProducts(int offset, int limit, LocalDate date) {
        String todayKey = MetricsKeys.PRODUCT_SCORE.getKey(date);
        return defaultRedisTemplate.opsForZSet().reverseRange(todayKey, offset, offset + limit - 1).stream()
                .map(Long::valueOf)
                .toList();
    }

    @Override
    public Long getTotalCount(LocalDate date) {
        String todayKey = MetricsKeys.PRODUCT_SCORE.getKey(date);
        return defaultRedisTemplate.opsForZSet().zCard(todayKey);
    }
}
