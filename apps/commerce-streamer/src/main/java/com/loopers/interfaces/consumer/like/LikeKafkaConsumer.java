package com.loopers.interfaces.consumer.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.metrics.MetricCriteria;
import com.loopers.application.metrics.MetricsFacade;
import com.loopers.config.kafka.KafkaConfig;
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
    private static final String LIKE_CONSUMER_GROUP = "like-consumer";
    private final MetricsFacade metricsFacade;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.liked}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = LIKE_CONSUMER_GROUP
    )
    public void consumeLikedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            LikeEvent.Liked event = objectMapper.readValue(message.value(), LikeEvent.Liked.class);

            MetricCriteria.IncrementLike cri =
                    new MetricCriteria.IncrementLike(event.eventId(), LIKE_CONSUMER_GROUP, event.toString(), event.productId());

            metricsFacade.incrementLikeCount(cri);
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${kafka.topics.like-canceled}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = LIKE_CONSUMER_GROUP
    )
    public void consumeLikeCanceledEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            LikeEvent.Canceled event = objectMapper.readValue(message.value(), LikeEvent.Canceled.class);

            MetricCriteria.DecrementLike cri =
                    new MetricCriteria.DecrementLike(event.eventId(), LIKE_CONSUMER_GROUP, event.toString(), event.productId());

            metricsFacade.decrementLikeCount(cri);
        }
        acknowledgment.acknowledge();
    }
}
