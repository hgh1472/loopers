package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentInfo pay(PaymentCommand.Pay command) {
        Payment payment = Payment.of(command);
        return PaymentInfo.of(paymentRepository.save(payment));
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
}
