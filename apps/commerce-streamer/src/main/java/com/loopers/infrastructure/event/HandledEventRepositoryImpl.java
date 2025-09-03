package com.loopers.infrastructure.event;

import com.loopers.domain.event.HandledEvent;
import com.loopers.domain.event.HandledEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HandledEventRepositoryImpl implements HandledEventRepository {
    private final HandledEventJpaRepository handledEventJpaRepository;

    @Override
    public HandledEvent save(HandledEvent handledEvent) {
        return handledEventJpaRepository.save(handledEvent);
    }
}
