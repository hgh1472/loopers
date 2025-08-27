package com.loopers.interfaces.scheduler;

import com.loopers.domain.payment.Payment;
import com.loopers.domain.payment.PaymentCommand;
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

    @Scheduled(cron = "0 0/1 * * * ?")
    public void syncPayment() {
        List<PaymentInfo.Transaction> transactionInfos = paymentService.getUnsyncedPendingPayments();
        for (PaymentInfo.Transaction info : transactionInfos) {
            switch (info.status()) {
                case Payment.Status.COMPLETED -> paymentService.success(new PaymentCommand.Success(info.transactionKey()));
                case Payment.Status.FAILED -> paymentService.fail(new PaymentCommand.Fail(info.transactionKey(), info.reason()));
            }
        }
    }
}
