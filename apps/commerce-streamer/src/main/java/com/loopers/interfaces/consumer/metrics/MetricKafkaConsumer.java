package com.loopers.interfaces.consumer.metrics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.application.metrics.MetricCriteria;
import com.loopers.application.metrics.MetricsFacade;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.interfaces.consumer.events.LikeEvent;
import com.loopers.interfaces.consumer.events.OrderEvent;
import com.loopers.interfaces.consumer.events.ProductEvent;
import com.loopers.message.KafkaMessage;
import java.io.IOException;
import java.util.ArrayList;
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
            topics = "${kafka.topics.like}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = CONSUMER_GROUP
    )
    public void consumeLikeEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
            throws IOException {
        List<MetricCriteria.IncrementLike> incrementLikes = new ArrayList<>();
        List<MetricCriteria.DecrementLike> decrementLikes = new ArrayList<>();

        for (ConsumerRecord<String, byte[]> message : messages) {
            KafkaMessage<LikeEvent.Like> event = objectMapper.readValue(message.value(), new TypeReference<>() {
            });
            if (event.getPayload().liked()) {
                MetricCriteria.IncrementLike cri = new MetricCriteria.IncrementLike(
                        event.getEventId(),
                        CONSUMER_GROUP,
                        event.getPayload().toString(),
                        Long.parseLong(event.getAggregateId()),
                        event.getTimestamp()
                );
                incrementLikes.add(cri);
            } else {
                MetricCriteria.DecrementLike cri = new MetricCriteria.DecrementLike(
                        event.getEventId(),
                        CONSUMER_GROUP,
                        event.getPayload().toString(),
                        Long.parseLong(event.getAggregateId()),
                        event.getTimestamp()
                );
                decrementLikes.add(cri);
            }
        }

        if (!incrementLikes.isEmpty()) {
            metricsFacade.incrementLikeCounts(incrementLikes);
        }
        if (!decrementLikes.isEmpty()) {
            metricsFacade.decrementLikeCounts(decrementLikes);
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
        List<MetricCriteria.IncrementSales> incrementSales = new ArrayList<>();
        for (ConsumerRecord<String, byte[]> message : messages) {
            KafkaMessage<OrderEvent.Paid> event = objectMapper.readValue(message.value(), new TypeReference<>() {
            });

            List<MetricCriteria.SaleLine> saleLines = event.getPayload().lines().stream()
                    .map(l -> new MetricCriteria.SaleLine(l.productId(), l.quantity()))
                    .toList();

            MetricCriteria.IncrementSales cri = new MetricCriteria.IncrementSales(
                    event.getEventId(),
                    CONSUMER_GROUP,
                    event.getPayload().toString(),
                    saleLines,
                    event.getTimestamp()
            );

            incrementSales.add(cri);
        }

        if (!incrementSales.isEmpty()) {
            metricsFacade.incrementSalesCounts(incrementSales);
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
        List<MetricCriteria.IncrementView> incrementViews = new ArrayList<>();
        for (ConsumerRecord<String, byte[]> message : messages) {
            ProductEvent.Viewed event = objectMapper.readValue(message.value(), ProductEvent.Viewed.class);

            MetricCriteria.IncrementView cri = new MetricCriteria.IncrementView(
                    event.eventId(),
                    CONSUMER_GROUP,
                    event.toString(),
                    event.productId(),
                    event.createdAt());

            incrementViews.add(cri);
        }

        if (!incrementViews.isEmpty()) {
            metricsFacade.incrementViewCounts(incrementViews);
        }

        acknowledgment.acknowledge();
    }
}
