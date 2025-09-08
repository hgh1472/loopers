package com.loopers.application.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.loopers.application.metrics.MetricsApplicationEvent.Type;
import com.loopers.domain.event.HandledEvent;
import com.loopers.domain.event.HandledEventRepository;
import com.loopers.domain.metrics.MetricsService;
import com.loopers.domain.metrics.ProductMetrics;
import com.loopers.domain.metrics.ProductMetricsRepository;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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
    @Autowired
    private ProductMetricsRepository productMetricsRepository;
    @MockitoSpyBean
    private MetricsApplicationEventPublisher eventPublisher;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("상품 좋아요 수 증가 시,")
    class IncrementLikes {
        @Test
        @DisplayName("같은 상품에 대한 좋아요 증가 이벤트가 여러 건 존재하면, 상품 별로 합산하여 좋아요 수를 증가시킨다.")
        void incrementLikeCounts_withSum() {
            String consumerGroup = "consumer-group";
            String payload = "{}";
            ZonedDateTime now = ZonedDateTime.now();
            MetricCriteria.IncrementLike cri1 = new MetricCriteria.IncrementLike("event-id-1", consumerGroup, payload, 1L, now);
            MetricCriteria.IncrementLike cri2 = new MetricCriteria.IncrementLike("event-id-2", consumerGroup, payload, 1L, now);

            metricsFacade.incrementLikeCounts(List.of(cri1, cri2));

            ProductMetrics productMetrics1 = productMetricsRepository.findByDailyMetrics(1L, now.toLocalDate())
                    .orElseThrow();

            assertThat(productMetrics1.getLikeCount()).isEqualTo(2L);
        }

        @Test
        @DisplayName("메트릭 업데이트 이벤트를 발행한다.")
        void publishUpdatedEvent() {
            String consumerGroup = "consumer-group";
            String payload = "{}";
            ZonedDateTime now = ZonedDateTime.now();
            MetricCriteria.IncrementLike cri1 = new MetricCriteria.IncrementLike("event-id-1", consumerGroup, payload, 1L, now);
            MetricCriteria.IncrementLike cri2 = new MetricCriteria.IncrementLike("event-id-2", consumerGroup, payload, 1L, now);
            MetricCriteria.IncrementLike cri3 = new MetricCriteria.IncrementLike("event-id-3", consumerGroup, payload, 2L, now);

            metricsFacade.incrementLikeCounts(List.of(cri1, cri2, cri3));

            verify(eventPublisher).publish(List.of(new MetricsApplicationEvent.Updated(1L, 2L, Type.LIKE, now.toLocalDate()),
                    new MetricsApplicationEvent.Updated(2L, 1L, Type.LIKE, now.toLocalDate())));
        }
    }

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
            List<MetricCriteria.SaleLine> lines1 = List.of(new MetricCriteria.SaleLine(1L, 2L));
            List<MetricCriteria.SaleLine> lines2 = List.of(new MetricCriteria.SaleLine(1L, 4L));
            MetricCriteria.IncrementSales cri1 = new MetricCriteria.IncrementSales(eventId, consumerGroup, payload, lines1, now);

            metricsFacade.incrementSalesCounts(List.of(cri1));

            verify(metricsService, never())
                    .incrementSalesCount(any());
        }

        @Test
        @DisplayName("상품 별로 판매량을 합산하여 증가시킨다.")
        void incrementSalesCounts_withSum() {
            String consumerGroup = "consumer-group";
            String payload = "{}";
            ZonedDateTime now = ZonedDateTime.now();
            List<MetricCriteria.SaleLine> lines1 = List.of(new MetricCriteria.SaleLine(1L, 2L));
            List<MetricCriteria.SaleLine> lines2 = List.of(new MetricCriteria.SaleLine(1L, 4L));
            MetricCriteria.IncrementSales cri1 = new MetricCriteria.IncrementSales("event-id-1", consumerGroup, payload, lines1, now);
            MetricCriteria.IncrementSales cri2 = new MetricCriteria.IncrementSales("event-id-2", consumerGroup, payload, lines2, now);

            metricsFacade.incrementSalesCounts(List.of(cri1, cri2));

            ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(1L, now.toLocalDate())
                    .orElseThrow();
            assertThat(productMetrics.getSalesCount()).isEqualTo(6L);
        }

        @Test
        @DisplayName("메트릭 업데이트 이벤트를 발행한다.")
        void publishUpdatedEvent() {
            String consumerGroup = "consumer-group";
            String payload = "{}";
            ZonedDateTime now = ZonedDateTime.now();
            List<MetricCriteria.SaleLine> lines1 = List.of(new MetricCriteria.SaleLine(1L, 2L));
            List<MetricCriteria.SaleLine> lines2 = List.of(new MetricCriteria.SaleLine(1L, 4L), new MetricCriteria.SaleLine(2L, 3L));
            MetricCriteria.IncrementSales cri1 = new MetricCriteria.IncrementSales("event-id-1", consumerGroup, payload, lines1, now);
            MetricCriteria.IncrementSales cri2 = new MetricCriteria.IncrementSales("event-id-2", consumerGroup, payload, lines2, now);

            metricsFacade.incrementSalesCounts(List.of(cri1, cri2));

            verify(eventPublisher).publish(List.of(new MetricsApplicationEvent.Updated(1L, 6L, Type.SALES, now.toLocalDate()),
                    new MetricsApplicationEvent.Updated(2L, 3L, Type.SALES, now.toLocalDate())));
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

            metricsFacade.incrementViewCounts(List.of(cri));

            verify(metricsService, never())
                    .incrementViewCount(any());
        }

        @Test
        @DisplayName("같은 상품에 대한 조회수 증가 이벤트가 여러 건 존재하면, 상품 별로 합산하여 조회수를 증가시킨다.")
        void incrementViewCounts_withSum() {
            String consumerGroup = "consumer-group";
            String payload = "{}";
            ZonedDateTime now = ZonedDateTime.now();
            MetricCriteria.IncrementView cri1 = new MetricCriteria.IncrementView("event-id-1", consumerGroup, payload, 1L, now);
            MetricCriteria.IncrementView cri2 = new MetricCriteria.IncrementView("event-id-2", consumerGroup, payload, 1L, now);

            metricsFacade.incrementViewCounts(List.of(cri1, cri2));

            ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(1L, now.toLocalDate())
                    .orElseThrow();

            assertThat(productMetrics.getViewCount()).isEqualTo(2L);
        }
    }

    @Test
    @DisplayName("메트릭 업데이트 이벤트를 발행한다.")
    void publishUpdatedEvent() {
        String consumerGroup = "consumer-group";
        String payload = "{}";
        ZonedDateTime now = ZonedDateTime.now();
        MetricCriteria.IncrementView cri1 = new MetricCriteria.IncrementView("event-id-1", consumerGroup, payload, 1L, now);
        MetricCriteria.IncrementView cri2 = new MetricCriteria.IncrementView("event-id-2", consumerGroup, payload, 1L, now);
        MetricCriteria.IncrementView cri3 = new MetricCriteria.IncrementView("event-id-3", consumerGroup, payload, 2L, now);

        metricsFacade.incrementViewCounts(List.of(cri1, cri2, cri3));

        verify(eventPublisher).publish(List.of(new MetricsApplicationEvent.Updated(1L, 2L, Type.VIEW, now.toLocalDate()),
                new MetricsApplicationEvent.Updated(2L, 1L, Type.VIEW, now.toLocalDate())));
    }
}
