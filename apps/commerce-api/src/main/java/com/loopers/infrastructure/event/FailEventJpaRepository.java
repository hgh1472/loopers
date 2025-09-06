package com.loopers.infrastructure.event;

import com.loopers.domain.event.FailEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailEventJpaRepository extends JpaRepository<FailEvent, Long> {
}
