package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PaymentTest {

    @Nested
    @DisplayName("Payment 생성 시,")
    class Create {

        @Test
        @DisplayName("결제 금액이 null인 경우, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenAmountIsNull() {
            UUID orderId = UUID.randomUUID();
            PaymentCommand.Pay cmd = new PaymentCommand.Pay(null, orderId, "SAMSUNG", "1234-1234-1234-1234");

            CoreException thrown = assertThrows(CoreException.class, () -> Payment.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 필수입니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "-1"})
        @DisplayName("결제 금액이 0 이하인 경우, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenAmountIsZeroOrNegative(String amount) {
            UUID orderId = UUID.randomUUID();
            PaymentCommand.Pay cmd = new PaymentCommand.Pay(new BigDecimal(amount), orderId, "SAMSUNG", "1234-1234-1234-1234");

            CoreException thrown = assertThrows(CoreException.class, () -> Payment.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "결제 금액은 0보다 커야 합니다."));
        }

        @Test
        @DisplayName("주문 ID가 null인 경우, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenOrderIdIsNull() {
            PaymentCommand.Pay cmd = new PaymentCommand.Pay(new BigDecimal("1000"), null, "SAMSUNG", "1234-1234-1234-1234");

            CoreException thrown = assertThrows(CoreException.class, () -> Payment.of(cmd));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "주문 ID는 필수입니다."));
        }

        @Test
        @DisplayName("결제 상태는 CREATED으로 생성된다.")
        void statusIsPending_whenPaymentIsCreated() {
            UUID orderId = UUID.randomUUID();
            PaymentCommand.Pay cmd = new PaymentCommand.Pay(new BigDecimal("1000"), orderId, "SAMSUNG", "1234-1234-1234-1234");

            Payment payment = Payment.of(cmd);

            assertThat(payment.getStatus()).isEqualTo(Payment.Status.CREATED);
        }
    }

    @Nested
    @DisplayName("결제 환불 시,")
    class Refund {
        @Test
        @DisplayName("결제 상태가 FAILED인 경우, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenPaymentStatusIsNotCompleted() {
            Card card = Card.of("1234-1234-1234-1234", "SAMSUNG");
            UUID orderId = UUID.randomUUID();
            Payment payment = new Payment(null, orderId, new BigDecimal("1000"), card, Payment.Status.FAILED, null);

            CoreException thrown = assertThrows(CoreException.class, payment::refund);

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "결제되지 않은 요청은 환불할 수 없습니다."));
        }
    }

    @Test
    @DisplayName("결제 실패 시, PENDING 상태가 아닌 경우, BAD_REQUEST 예외가 발생한다.")
    void throwBadRequestException_whenPaymentStatusIsNotPending() {
        Card card = Card.of("1234-1234-1234-1234", "SAMSUNG");
        UUID orderId = UUID.randomUUID();
        Payment payment = new Payment(null, orderId, new BigDecimal("1000"), card, Payment.Status.COMPLETED, null);

        CoreException thrown = assertThrows(CoreException.class, () -> payment.fail("결제 실패 사유"));

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "결제는 대기 상태에서만 실패할 수 있습니다."));
    }

    @Nested
    @DisplayName("결제 요청 성공 시,")
    class SuccessRequest {

        @Test
        @DisplayName("결제 상태가 PENDING이 아닌 경우, CONFLICT 예외가 발생한다.")
        void throwConflictException_whenPaymentStatusIsNotCreated() {
            Card card = Card.of("1234-1234-1234-1234", "SAMSUNG");
            UUID orderId = UUID.randomUUID();
            Payment payment = new Payment(null, orderId, new BigDecimal("1000"), card, Payment.Status.PENDING, null);

            CoreException thrown = assertThrows(CoreException.class, () -> payment.successRequest("transactionKey123"));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "결제 요청 성공 처리는 생성 상태에서만 성공할 수 있습니다."));
        }

        @Test
        @DisplayName("결제 상태가 PENDING으로 바뀐다.")
        void statusIsPending_whenPaymentRequestIsSuccessful() {
            Card card = Card.of("1234-1234-1234-1234", "SAMSUNG");
            UUID orderId = UUID.randomUUID();
            Payment payment = new Payment(null, orderId, new BigDecimal("1000"), card, Payment.Status.CREATED, null);

            payment.successRequest("transactionKey123");

            assertThat(payment.getStatus()).isEqualTo(Payment.Status.PENDING);
            assertThat(payment.getTransactionKey()).isEqualTo("transactionKey123");
        }

        @Test
        @DisplayName("결제 요청 성공 시, transactionKey가 설정된다.")
        void transactionKeyIsSet_whenPaymentRequestIsSuccessful() {
            Card card = Card.of("1234-1234-1234-1234", "SAMSUNG");
            UUID orderId = UUID.randomUUID();
            Payment payment = new Payment(null, orderId, new BigDecimal("1000"), card, Payment.Status.CREATED, null);

            payment.successRequest("transactionKey123");

            assertThat(payment.getTransactionKey()).isEqualTo("transactionKey123");
        }
    }

    @Nested
    @DisplayName("결제 요청 실패 시,")
    class FailRequest {

        @Test
        @DisplayName("결제 상태가 PENDING이 아닌 경우, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenPaymentStatusIsNotPending() {
            Card card = Card.of("1234-1234-1234-1234", "SAMSUNG");
            UUID orderId = UUID.randomUUID();
            Payment payment = new Payment(null, orderId, new BigDecimal("1000"), card, Payment.Status.COMPLETED, null);

            CoreException thrown = assertThrows(CoreException.class, () -> payment.fail("결제 실패 사유"));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "결제는 대기 상태에서만 실패할 수 있습니다."));
        }

        @Test
        @DisplayName("결제 상태가 FAILED로 바뀐다.")
        void statusIsFailed_whenPaymentRequestFails() {
            Card card = Card.of("1234-1234-1234-1234", "SAMSUNG");
            UUID orderId = UUID.randomUUID();
            Payment payment = new Payment(null, orderId, new BigDecimal("1000"), card, Payment.Status.PENDING, null);

            payment.fail("결제 실패 사유");

            assertThat(payment.getStatus()).isEqualTo(Payment.Status.FAILED);
            assertThat(payment.getReason()).isEqualTo("결제 실패 사유");
        }
    }

}
