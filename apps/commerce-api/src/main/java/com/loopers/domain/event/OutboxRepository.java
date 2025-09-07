package com.loopers.domain.event;

import java.util.List;
import java.util.Optional;

public interface OutboxRepository {
    Outbox save(Outbox outbox);

    Optional<Outbox> findByEventId(String eventId);

    List<Outbox> findOutboxesByStatus(Outbox.Status status);
}
