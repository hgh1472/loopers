package com.loopers.infrastructure.payment.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import com.loopers.domain.payment.GatewayResponse;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCommand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
    private LoopersRequestV1Client loopersRequestV1Client;
    @MockitoBean
    private LoopersGetV1Client loopersGetV1Client;

    @Nested
    @DisplayName("PG 결제 요청 시,")
    class Request {

        @Test
        @DisplayName("결제 요청에 예외가 발생하면, 1회 재시도 한다.")
        void retry_whenPgFails() {
            given(loopersRequestV1Client.requestPayment(any(), anyString()))
                    .willThrow(FeignException.class);
            PaymentCommand.Pay command = new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111");

            GatewayResponse.Request request = loopersPaymentGateway.request(Payment.of(command));

            verify(loopersRequestV1Client, times(2)).requestPayment(any(), anyString());
        }

        @Test
        @DisplayName("실패 후 재시도 요청도 실패하면, 서킷 브레이커 폴백 메서드를 호출한다.")
        void fallback_whenRetryFails() {
            given(loopersRequestV1Client.requestPayment(any(), anyString()))
                    .willThrow(FeignException.class);
            PaymentCommand.Pay command = new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111");

            GatewayResponse.Request request = loopersPaymentGateway.request(Payment.of(command));

            verify(loopersRequestV1Client, times(2)).requestPayment(any(), anyString());
            verify(loopersPaymentGateway, times(1)).requestFallback(any(), any());
        }
    }

    @Nested
    @DisplayName("PG 결제 정보 조회 시,")
    class GetTransaction {

        @Test
        @DisplayName("결제 정보 조회에 예외가 발생하면, 1회 재시도한다.")
        void retry_whenGetTransactionFails() {
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111"));
            payment.successRequest("TX-KEY");
            given(loopersGetV1Client.getTransaction(eq(payment.getTransactionKey()), anyString()))
                    .willThrow(FeignException.class);

            CoreException thrown = assertThrows(CoreException.class, () -> loopersPaymentGateway.getTransaction(payment));

            verify(loopersGetV1Client, times(2)).getTransaction(anyString(), anyString());
            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.INTERNAL_ERROR, "결제 정보 조회에 실패하였습니다."));
        }

        @Test
        @DisplayName("재시도 요청도 실패한다면, 서킷 브레이커의 폴백으로 넘어간다.")
        void circuitBreaker_whenRetryFails() {
            Payment payment = Payment.of(new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111"));
            payment.successRequest("TX-KEY");
            given(loopersGetV1Client.getTransaction(eq(payment.getTransactionKey()), anyString()))
                    .willThrow(FeignException.class);

            CoreException thrown = assertThrows(CoreException.class, () -> loopersPaymentGateway.getTransaction(payment));

            verify(loopersGetV1Client, times(2)).getTransaction(anyString(), anyString());
            verify(loopersPaymentGateway, times(1)).getTransactionFallback(any(), any());
            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.INTERNAL_ERROR, "결제 정보 조회에 실패하였습니다."));
        }
    }
}
