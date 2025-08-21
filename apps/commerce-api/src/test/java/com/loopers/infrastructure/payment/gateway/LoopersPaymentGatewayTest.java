package com.loopers.infrastructure.payment.gateway;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCommand;
import feign.FeignException;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class LoopersPaymentGatewayIntegrationTest {

    @MockitoSpyBean
    private LoopersPaymentGateway loopersPaymentGateway;
    @MockitoBean
    private LoopersV1Client loopersV1Client;

    @Nested
    @DisplayName("PG 결제 요청 시,")
    class Request {

        @Test
        @DisplayName("결제 요청에 예외가 발생하면, 1회 재시도 한다.")
        void retry_whenPgFails() {
            given(loopersV1Client.request(any(), anyString()))
                    .willThrow(FeignException.class);
            PaymentCommand.Pay command = new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111");

            GatewayResponse.Request request = loopersPaymentGateway.request(Payment.of(command));

            verify(loopersV1Client, times(2)).request(any(), anyString());
        }

        @Test
        @DisplayName("실패 후 재시도 요청도 실패하면, 실패 응답 폴백 메서드를 호출한다.")
        void fallback_whenRetryFails() {
            given(loopersV1Client.request(any(), anyString()))
                    .willThrow(FeignException.class);
            PaymentCommand.Pay command = new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111");

            GatewayResponse.Request request = loopersPaymentGateway.request(Payment.of(command));

            verify(loopersV1Client, times(2)).request(any(), anyString());
            verify(loopersPaymentGateway, times(1)).requestFallback(any(), any());
        }
    }
}
