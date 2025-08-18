package com.loopers.domain.payment;

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
}
