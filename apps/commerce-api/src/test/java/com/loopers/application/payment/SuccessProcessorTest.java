package com.loopers.application.payment;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.payment.PaymentService;
import com.loopers.domain.point.PointService;
import com.loopers.domain.stock.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuccessProcessorTest {

    @InjectMocks
    private SuccessProcessor successProcessor;
    @Mock
    StockService stockService;
    @Mock
    PointService pointService;
    @Mock
    PaymentService paymentService;
    @Mock
    OrderService orderService;

    @Nested
    @DisplayName("주문 결제 성공 처리 시,")
    class Process {

    }
}
