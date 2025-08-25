package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;

    @Transactional
    public PaymentInfo pay(PaymentCommand.Pay command) {
        Payment payment = Payment.of(command);
        GatewayResponse.Request response = paymentGateway.request(payment, Card.of(command.cardNo(), command.cardType()));
        if (response.isSuccess()) {
            payment.successRequest(response.transactionKey());
        } else {
            payment.failRequest();
        }
        return PaymentInfo.of(paymentRepository.save(payment));
    }

    public List<PaymentInfo.Transaction> getUnsyncedPendingPayments() {
        List<Payment> pendingPayments = paymentRepository.findPendingPayments();

        return pendingPayments.stream()
                .map(pendingPayment -> {
                    try {
                        return paymentGateway.getTransaction(pendingPayment);
                    } catch (CoreException e) {
                        return new GatewayResponse.Transaction(
                                Payment.Status.PENDING,
                                pendingPayment.getTransactionKey(),
                                pendingPayment.getOrderId(),
                                pendingPayment.getAmount().longValue(),
                                pendingPayment.getReason()
                        );
                    }
                })
                .filter(transaction -> !transaction.status().equals(Payment.Status.PENDING))
                .map(PaymentInfo.Transaction::of)
                .toList();
    }

    @Transactional
    public RefundInfo refund(PaymentCommand.Refund command) {
        Payment payment = paymentRepository.findByTransactionKey(command.transactionKey())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 정보가 없습니다."));
        Refund refund = payment.refund();
        return RefundInfo.of(paymentRepository.save(refund));
    }

    @Transactional
    public PaymentInfo success(PaymentCommand.Success command) {
        Payment payment = paymentRepository.findByTransactionKey(command.transactionKey())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 정보가 없습니다."));
        payment.success();
        return PaymentInfo.of(payment);
    }

    @Transactional
    public PaymentInfo fail(PaymentCommand.Fail command) {
        Payment payment = paymentRepository.findByTransactionKey(command.transactionKey())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당하는 결제 정보가 없습니다."));
        payment.fail(command.reason());
        return PaymentInfo.of(payment);
    }

    public void check() {
    }
}
