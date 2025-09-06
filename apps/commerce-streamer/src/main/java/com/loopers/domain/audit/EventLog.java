package com.loopers.domain.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.Getter;

@Entity
@Getter
@Table(name = "event_log")
public class EventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "received_at", nullable = false)
    private ZonedDateTime receivedAt;

    protected EventLog() {
    }

    public EventLog(String eventId, String eventName, String payload, ZonedDateTime createdAt) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.receivedAt = ZonedDateTime.now();
    }
}
