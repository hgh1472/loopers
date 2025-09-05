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

@Entity
@Getter
@Table(name = "fail_event")
public class FailEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_topic", nullable = false)
    private String topic;

    @Column(name = "event_key")
    private String eventKey;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Enumerated
    private Status status;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    protected FailEvent() {
    }

    public FailEvent(String eventId, String topic, String eventKey, String payload, ZonedDateTime createdAt) {
        this.eventId = eventId;
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.status = Status.FAILED;
        this.createdAt = createdAt;
    }

    public enum Status {
        FAILED,
        SUCCESS
    }
}
