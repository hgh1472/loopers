package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.Weight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeightJpaRepository extends JpaRepository<Weight, Long> {
    Weight findFirstByOrderByCreatedAtDesc();

    Weight findByActivateTrue();
}
