package com.loopers.domain.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
@Entity
@Table(name = "outbox")
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_topic", nullable = false)
    private String topic;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Enumerated
    private Status status;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    protected Outbox() {
    }

    public Outbox(String eventId, String topic, String aggregateId, String payload, ZonedDateTime createdAt) {
        this.eventId = eventId;
        this.topic = topic;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.status = Status.PENDING;
        this.createdAt = createdAt;
    }

    public void fail() {
        this.status = Status.FAILED;
    }

    public void success() {
        this.status = Status.SUCCESS;
    }

    public enum Status {
        PENDING,
        SUCCESS,
        FAILED
    }
}
