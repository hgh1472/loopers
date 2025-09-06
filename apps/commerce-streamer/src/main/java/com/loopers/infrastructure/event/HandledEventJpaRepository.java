package com.loopers.infrastructure.event;

import com.loopers.domain.event.HandledEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HandledEventJpaRepository extends JpaRepository<HandledEvent, Long> {
}
