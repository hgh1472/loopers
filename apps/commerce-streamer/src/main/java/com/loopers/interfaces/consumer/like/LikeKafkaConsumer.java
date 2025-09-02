package com.loopers.interfaces.consumer.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.metrics.MetricCommand;
import com.loopers.domain.metrics.MetricService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeKafkaConsumer {
    private final MetricService metricService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.liked}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consumeLikedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            LikeEvent.Liked event = objectMapper.readValue(message.value(), LikeEvent.Liked.class);
            metricService.incrementsLikeCount(new MetricCommand.IncrementLike(event.productId()));
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${kafka.topics.like-canceled}",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void consumeLikeCanceledEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            LikeEvent.Canceled event = objectMapper.readValue(message.value(), LikeEvent.Canceled.class);
            metricService.decrementsLikeCount(new MetricCommand.DecrementLike(event.productId()));
        }
        acknowledgment.acknowledge();
    }
}
