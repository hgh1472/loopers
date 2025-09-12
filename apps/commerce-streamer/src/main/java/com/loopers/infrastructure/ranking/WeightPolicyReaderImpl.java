package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.WeightPolicyReader;
import com.loopers.key.WeightKeys;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeightPolicyReaderImpl implements WeightPolicyReader {
    private final RedisTemplate<String, String> defaultRedisTemplate;
    private final WeightJpaRepository weightJpaRepository;

    @Override
    @CircuitBreaker(name = "likeWeight", fallbackMethod = "getLikeWeightFallback")
    public Double getLikeWeight(LocalDate date) {
        String value = (String) defaultRedisTemplate.opsForHash().get(WeightKeys.WEIGHT.getKey(date), "likes");
        if (value == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "Redis에 좋아요 가중치 정보가 존재하지 않습니다.");
        }
        return Double.parseDouble(value);
    }

    @Override
    @CircuitBreaker(name = "viewWeight", fallbackMethod = "getViewWeightFallback")
    public Double getViewWeight(LocalDate date) {
        String value = (String) defaultRedisTemplate.opsForHash().get(WeightKeys.WEIGHT.getKey(date), "views");
        if (value == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "Redis에 조회수 가중치 정보가 존재하지 않습니다.");
        }
        return Double.parseDouble(value);
    }

    @Override
    @CircuitBreaker(name = "salesWeight", fallbackMethod = "getSalesWeightFallback")
    public Double getSalesWeight(LocalDate date) {
        String value = (String) defaultRedisTemplate.opsForHash().get(WeightKeys.WEIGHT.getKey(date), "sales");
        if (value == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "Redis에 판매량 가중치 정보가 존재하지 않습니다.");
        }
        return Double.parseDouble(value);
    }

    public Double getLikeWeightFallback(LocalDate date, Throwable throwable) {
        log.error("Redis 내 가중치 조회 실패: {}", throwable.getMessage());
        return weightJpaRepository.findByActivateTrue().getLikeWeight();
    }

    public Double getViewWeightFallback(LocalDate date, Throwable throwable) {
        log.error("Redis 내 가중치 조회 실패: {}", throwable.getMessage());
        return weightJpaRepository.findByActivateTrue().getViewWeight();
    }

    public Double getSalesWeightFallback(LocalDate date, Throwable throwable) {
        log.error("Redis 내 가중치 조회 실패: {}", throwable.getMessage());
        return weightJpaRepository.findByActivateTrue().getSalesWeight();
    }
}
