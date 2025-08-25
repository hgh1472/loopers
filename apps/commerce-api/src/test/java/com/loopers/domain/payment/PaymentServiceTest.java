package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentGateway paymentGateway;

    @Nested
    @DisplayName("결제 요청 시,")
    class Pay {

        @Test
        @DisplayName("PG사에 결제를 요청한다.")
        void requestsPaymentToGateway() {
            PaymentCommand.Pay command = new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111");
            given(paymentGateway.request(any(), any())).willReturn(new GatewayResponse.Request(true, "transaction-key"));
            given(paymentRepository.save(any(Payment.class))).willReturn(Payment.of(command));

            PaymentInfo paymentInfo = paymentService.pay(command);

            verify(paymentGateway, times(1)).request(any(), any());
        }
    }

    @Nested
    @DisplayName("동기화 되지 않은 PENDING 결제 조회 시,")
    class GetUnsyncedPendingPayments {

        @Test
        @DisplayName("PG사에 결제 정보를 요청한다.")
        void requestsTransactionFromGateway() {
            Payment pending = Payment.of(new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111"));
            pending.successRequest("TX-KEY1");
            Payment completed = Payment.of(new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111"));
            completed.successRequest("TX-KEY2");
            Payment failed = Payment.of(new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111"));
            failed.successRequest("TX-KEY3");
            given(paymentRepository.findPendingPayments())
                    .willReturn(List.of(pending, completed, failed));
            given(paymentGateway.getTransaction(pending))
                    .willReturn(new GatewayResponse.Transaction(Payment.Status.PENDING, "TX-KEY1", pending.getOrderId(), 1000L, null));
            given(paymentGateway.getTransaction(completed))
                    .willReturn(new GatewayResponse.Transaction(Payment.Status.COMPLETED, "TX-KEY1", completed.getOrderId(), 1000L, null));
            given(paymentGateway.getTransaction(failed))
                    .willReturn(new GatewayResponse.Transaction(Payment.Status.FAILED, "TX-KEY1", failed.getOrderId(), 1000L, null));

            List<PaymentInfo.Transaction> unsyncedPendingPayments = paymentService.getUnsyncedPendingPayments();

            assertThat(unsyncedPendingPayments).hasSize(2);
            assertThat(unsyncedPendingPayments)
                    .extracting(PaymentInfo.Transaction::status)
                    .containsExactlyInAnyOrder(Payment.Status.FAILED, Payment.Status.COMPLETED);
        }
    }

    @Nested
    @DisplayName("결제 환불 처리 시,")
    class Refund {

        @Test
        @DisplayName("결제 정보가 존재하지 않으면, NOT_FOUND 예외를 발생시킨다.")
        void throwsNotFoundException_whenPaymentNotFound() {
            PaymentCommand.Refund command = new PaymentCommand.Refund("non-existent-transaction-key");
            given(paymentRepository.findByTransactionKey(command.transactionKey()))
                    .willReturn(Optional.empty());

            CoreException thrown = assertThrows(CoreException.class, () -> paymentService.refund(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 정보가 없습니다."));
        }
    }
}
