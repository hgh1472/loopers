package com.loopers.interfaces.scheduler;

import com.loopers.application.payment.PaymentCriteria;
import com.loopers.application.payment.PaymentFacade;
import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentInfo;
import com.loopers.domain.payment.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {

    private final PaymentService paymentService;
    private final PaymentFacade paymentFacade;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void syncPayment() {
        List<PaymentInfo.Transaction> transactionInfos = paymentService.getUnsyncedPendingPayments();
        for (PaymentInfo.Transaction info : transactionInfos) {
            switch (info.status()) {
                case Payment.Status.COMPLETED ->
                        paymentFacade.success(new PaymentCriteria.Success(info.transactionKey(), info.orderId()));
                case Payment.Status.FAILED ->
                        paymentFacade.fail(new PaymentCriteria.Fail(info.transactionKey(), info.orderId(), info.reason()));
            }
        }
    }
}
