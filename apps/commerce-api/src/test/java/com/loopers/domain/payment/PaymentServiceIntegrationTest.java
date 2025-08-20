package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.loopers.infrastructure.payment.gateway.LoopersPaymentGateway;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @MockitoBean
    private LoopersPaymentGateway paymentGateway;

    @Nested
    @DisplayName("결제 요청 시,")
    class Pay {

        @Test
        @DisplayName("요청이 성공한다면, 트랜잭션 키가 발급된다.")
        void updateTransactionKey_whenRequestSuccess() {
            PaymentCommand.Pay command = new PaymentCommand.Pay(new BigDecimal("1000"), UUID.randomUUID(), "SAMSUNG", "1111-1111-1111-1111");
            given(paymentGateway.request(any()))
                    .willReturn(new GatewayResponse.Request(true, "transactionKey123"));

            PaymentInfo paymentInfo = paymentService.pay(command);

            assertThat(paymentInfo.transactionKey()).isEqualTo("transactionKey123");
        }
    }
}
