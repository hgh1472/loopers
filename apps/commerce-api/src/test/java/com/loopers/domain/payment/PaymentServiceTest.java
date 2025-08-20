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
            given(paymentGateway.request(any())).willReturn(new GatewayResponse.Request(true, "transaction-key"));
            given(paymentRepository.save(any(Payment.class))).willReturn(Payment.of(command));

            PaymentInfo paymentInfo = paymentService.pay(command);

            verify(paymentGateway, times(1)).request(any());
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
