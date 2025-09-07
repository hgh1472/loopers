package com.loopers.domain.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.ZonedDateTime;

@Entity
@Table(name = "handled_event", uniqueConstraints = {
        @UniqueConstraint(name = "uk_event_consumer", columnNames = {"event_id", "consumer_group"})
})
public class HandledEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "consumer_group", nullable = false)
    private String consumerGroup;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "received_at", nullable = false)
    private ZonedDateTime receivedAt;

    protected HandledEvent() {
    }

    public HandledEvent(String eventId, String consumerGroup, String payload, ZonedDateTime createdAt) {
        this.eventId = eventId;
        this.consumerGroup = consumerGroup;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.receivedAt = ZonedDateTime.now();
    }
}
