package com.loopers.infrastructure.event;

import com.loopers.domain.event.Outbox;
import com.loopers.domain.event.OutboxRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {
    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public Optional<Outbox> findByEventId(String eventId) {
        return outboxJpaRepository.findByEventId(eventId);
    }

    @Override
    public List<Outbox> findOutboxesByStatus(Outbox.Status status) {
        return outboxJpaRepository.findByStatus(status);
    }
}
