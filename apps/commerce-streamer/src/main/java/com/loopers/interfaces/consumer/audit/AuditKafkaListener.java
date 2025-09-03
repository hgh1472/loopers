package com.loopers.interfaces.consumer.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.audit.AuditCommand;
import com.loopers.domain.audit.AuditService;
import com.loopers.interfaces.consumer.metrics.LikeEvent;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditKafkaListener {
    private static final String AUDIT_CONSUMER_GROUP = "audit-consumer";
    private final ObjectMapper objectMapper;
    private final AuditService auditService;

    @KafkaListener(
            topics = "${kafka.topics.liked}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = AUDIT_CONSUMER_GROUP
    )
    void consumeLikedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment) throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            LikeEvent.Liked event = objectMapper.readValue(message.value(), LikeEvent.Liked.class);
            auditService.save(new AuditCommand.Audit(event.eventId(), event.getClass().getName(), event.toString()));
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${kafka.topics.like-canceled}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = AUDIT_CONSUMER_GROUP
    )
    void consumeLikeCanceledEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            LikeEvent.Canceled event = objectMapper.readValue(message.value(), LikeEvent.Canceled.class);
            auditService.save(new AuditCommand.Audit(event.eventId(), event.getClass().getName(), event.toString()));
        }
        acknowledgment.acknowledge();
    }
}
