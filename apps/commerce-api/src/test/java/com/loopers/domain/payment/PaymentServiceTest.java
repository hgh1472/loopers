package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.*;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;

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
