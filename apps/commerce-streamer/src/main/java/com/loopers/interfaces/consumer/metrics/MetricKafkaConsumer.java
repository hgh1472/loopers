package com.loopers.interfaces.consumer.metrics;

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
public class MetricKafkaConsumer {
    private static final String CONSUMER_GROUP = "metrics-consumer";

    private final MetricsFacade metricsFacade;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.topics.liked}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = CONSUMER_GROUP
    )
    public void consumeLikedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            LikeEvent.Liked event = objectMapper.readValue(message.value(), LikeEvent.Liked.class);

            MetricCriteria.IncrementLike cri =
                    new MetricCriteria.IncrementLike(event.eventId(), CONSUMER_GROUP, event.toString(), event.productId(), event.createdAt());

            metricsFacade.incrementLikeCount(cri);
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${kafka.topics.like-canceled}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = CONSUMER_GROUP
    )
    public void consumeLikeCanceledEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            LikeEvent.Canceled event = objectMapper.readValue(message.value(), LikeEvent.Canceled.class);

            MetricCriteria.DecrementLike cri =
                    new MetricCriteria.DecrementLike(event.eventId(), CONSUMER_GROUP, event.toString(), event.productId(), event.createdAt());

            metricsFacade.decrementLikeCount(cri);
        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${kafka.topics.order-paid}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = CONSUMER_GROUP
    )
    public void consumeOrderPaidEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            OrderEvent.Paid event = objectMapper.readValue(message.value(), OrderEvent.Paid.class);

            for (OrderEvent.Line line : event.lines()) {
                MetricCriteria.IncrementSales cri = new MetricCriteria.IncrementSales(
                        event.eventId(),
                        CONSUMER_GROUP,
                        event.toString(),
                        line.productId(),
                        line.quantity(),
                        event.createdAt()
                );
                metricsFacade.incrementSalesCount(cri);
            }

        }
        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = "${kafka.topics.product-viewed}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = CONSUMER_GROUP
    )
    void consumeProductViewedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            ProductEvent.Viewed event = objectMapper.readValue(message.value(), ProductEvent.Viewed.class);

            MetricCriteria.IncrementView cri =
                    new MetricCriteria.IncrementView(event.eventId(),
                            CONSUMER_GROUP,
                            event.toString(),
                            event.productId(),
                            event.createdAt());

            metricsFacade.incrementViewCount(cri);
        }
        acknowledgment.acknowledge();
    }
}
