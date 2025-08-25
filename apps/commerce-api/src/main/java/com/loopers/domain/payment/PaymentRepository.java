package com.loopers.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Refund save(Refund refund);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByTransactionKey(String transactionKey);

    Optional<Refund> findRefundByPaymentId(Long paymentId);

    List<Payment> findPendingPayments();
}
