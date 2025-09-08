package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.WeightPolicyReader;
import com.loopers.key.WeightKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeightPolicyReaderImpl implements WeightPolicyReader {
    private final RedisTemplate<String, String> defaultRedisTemplate;

    // TODO: Weight 조회 실패 시, 디폴트 값 설정 필요 (ex. 서킷 브레이커)

    @Override
    public Double getLikeWeight() {
        return Double.parseDouble(
                (String) defaultRedisTemplate.opsForHash().get(WeightKeys.WEIGHT.getKey(), WeightKeys.LIKE.getKey()));
    }

    @Override
    public Double getViewWeight() {
        return Double.parseDouble(
                (String) defaultRedisTemplate.opsForHash().get(WeightKeys.WEIGHT.getKey(), WeightKeys.VIEW.getKey()));
    }

    @Override
    public Double getSalesWeight() {
        return Double.parseDouble(
                (String) defaultRedisTemplate.opsForHash().get(WeightKeys.WEIGHT.getKey(), WeightKeys.SALES.getKey()));
    }
}
