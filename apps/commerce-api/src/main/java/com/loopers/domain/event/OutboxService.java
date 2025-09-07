package com.loopers.domain.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final GlobalEventPublisher globalEventPublisher;

    public Outbox save(EventCommand.Save cmd) {
        Outbox outbox = new Outbox(cmd.eventId(), cmd.topic(), cmd.aggregateId(), cmd.payload(), cmd.createdAt());
        return outboxRepository.save(outbox);
    }

    public void retryFailedEvents() {
        List<Outbox> outboxes = outboxRepository.findOutboxesByStatus(Outbox.Status.FAILED);
        for (Outbox outbox : outboxes) {
            globalEventPublisher.publish(outbox);
        }
    }
}
