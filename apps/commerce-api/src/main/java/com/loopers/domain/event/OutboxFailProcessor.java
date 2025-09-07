package com.loopers.domain.event;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxFailProcessor {
    private final OutboxRepository outboxRepository;

    @Transactional
    public void process(String eventId, String message) {
        log.error("카프카 메세지를 전송하는데 실패하였습니다. eventId: {}, error: {}", eventId, message);
        Outbox outbox = outboxRepository.findByEventId(eventId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 이벤트입니다."));
        outbox.fail();
    }
}
