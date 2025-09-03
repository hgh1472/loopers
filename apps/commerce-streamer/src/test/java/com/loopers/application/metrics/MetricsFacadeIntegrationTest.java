package com.loopers.application.metrics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.loopers.domain.event.HandledEvent;
import com.loopers.domain.event.HandledEventRepository;
import com.loopers.domain.metrics.MetricsService;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class MetricsFacadeIntegrationTest {
    @Autowired
    private MetricsFacade metricsFacade;
    @MockitoSpyBean
    private MetricsService metricsService;
    @Autowired
    private HandledEventRepository handledEventRepository;

    @Nested
    @DisplayName("상품 판매량 증가 시,")
    class IncrementSales {
        @Test
        @DisplayName("이미 해당 이벤트에 대한 처리 이력이 존재하면, 아무런 작업도 수행하지 않는다.")
        void ignore_whenEventDuplicate() {
            String eventId = "event-id";
            String consumerGroup = "consumer-group";
            String payload = "{}";
            ZonedDateTime now = ZonedDateTime.now();
            HandledEvent handledEvent = new HandledEvent(eventId, consumerGroup, payload, now);
            handledEventRepository.save(handledEvent);
            MetricCriteria.IncrementSales cri = new MetricCriteria.IncrementSales(eventId, consumerGroup, payload, 1L, 2L, now);

            metricsFacade.incrementSalesCount(cri);

            verify(metricsService, never())
                    .incrementSalesCount(any());
        }
    }

    @Nested
    @DisplayName("상품 조회수 증가 시,")
    class IncrementView {
        @Test
        @DisplayName("이미 해당 이벤트에 대한 처리 이력이 존재하면, 아무런 작업도 수행하지 않는다.")
        void ignore_whenEventDuplicate() {
            String eventId = "event-id";
            String consumerGroup = "consumer-group";
            String payload = "{}";
            ZonedDateTime now = ZonedDateTime.now();
            HandledEvent handledEvent = new HandledEvent(eventId, consumerGroup, payload, now);
            handledEventRepository.save(handledEvent);
            MetricCriteria.IncrementView cri = new MetricCriteria.IncrementView(eventId, consumerGroup, payload, 1L, now);

            metricsFacade.incrementViewCount(cri);

            verify(metricsService, never())
                    .incrementViewCount(any());
        }
    }
}
