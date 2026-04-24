package com.company.ordermanagement.service.impl;

import com.company.ordermanagement.model.entity.Payment;
import com.company.ordermanagement.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

// Activity 3 (Code Review) — This class is part of the "Payment Retry Mechanism" PR.
// It consumes from the DLQ and re-attempts failed payments.
//
// Bugs for the reviewer to find:
//   1. @Autowired field injection (line 29) — should be constructor injection
//   2. No idempotency check — processRetry() doesn't check if already COMPLETED
//   3. PII risk: the logged paymentEvent map may contain paymentMethod with card data
@Service
@Slf4j
public class RetryablePaymentService {

    // BUG 1: field injection — should be constructor injection
    // This violates the CLAUDE.md rule and will be caught by pre-commit hook
    @org.springframework.beans.factory.annotation.Autowired
    private PaymentRepository paymentRepository;

    @KafkaListener(
        topics = "order-service.payment.processed.dlq",
        groupId = "order-management-service-retry-group"
    )
    public void processRetry(Map<String, Object> paymentEvent) {
        // BUG 3: logging paymentEvent which may contain paymentMethod (card data)
        log.info("Retrying failed payment event: {}", paymentEvent);

        String paymentIdStr = (String) paymentEvent.get("paymentId");
        if (paymentIdStr == null) {
            log.error("No paymentId in DLQ event, dropping message");
            return;
        }

        UUID paymentId = UUID.fromString(paymentIdStr);

        // BUG 2: no idempotency check — if this message was delivered twice
        // (e.g. at-least-once delivery), we'd attempt the payment twice
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        payment.setRetryCount(payment.getRetryCount() + 1);
        payment.setStatus(Payment.PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        log.info("Re-queued payment for retry: paymentId={}, attempt={}",
                paymentId, payment.getRetryCount());
    }
}
