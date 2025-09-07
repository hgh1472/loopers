package com.loopers.infrastructure.event;

import com.loopers.domain.event.Outbox;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {
    Optional<Outbox> findByEventId(String eventId);

    List<Outbox> findByStatus(Outbox.Status status);
}
