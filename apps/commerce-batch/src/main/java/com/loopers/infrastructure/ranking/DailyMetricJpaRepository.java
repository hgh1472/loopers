package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.DailyMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyMetricJpaRepository extends JpaRepository<DailyMetric, Long> {
}
