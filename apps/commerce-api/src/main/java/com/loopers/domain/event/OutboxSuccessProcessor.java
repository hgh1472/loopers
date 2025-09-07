package com.loopers.domain.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxSuccessProcessor {
    private final OutboxRepository outboxRepository;

    @Transactional
    public void process(String eventId) {
        Outbox outbox = outboxRepository.findByEventId(eventId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이벤트입니다."));
        outbox.success();
    }
}
