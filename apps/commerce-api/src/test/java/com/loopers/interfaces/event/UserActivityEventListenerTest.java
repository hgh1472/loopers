package com.loopers.interfaces.event;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.domain.like.LikeEvent;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@SpringBootTest
@RecordApplicationEvents
class UserActivityEventListenerTest {

    @MockitoSpyBean
    private UserActivityEventListener userActivityEventListener;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("UserActivityEvent의 구현체에 해당하는 이벤트가 발행 됐을 때, 로그를 남긴다.")
    void handle_userActivityEvent() {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        eventPublisher.publishEvent(new LikeEvent.Liked(UUID.randomUUID(), 1L, 1L, ZonedDateTime.now()));
        transactionManager.commit(transaction);

        await()
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        verify(userActivityEventListener, times(1))
                                .handle(any())
                );
    }
}
