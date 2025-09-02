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

    protected EventLog() {
    }

    public EventLog(String eventId, String eventName, String payload) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.payload = payload;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }
}
