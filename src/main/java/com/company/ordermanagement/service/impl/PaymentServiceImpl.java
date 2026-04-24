package com.company.ordermanagement.service.impl;

import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.model.entity.OrderItem;
import com.company.ordermanagement.model.entity.Payment;
import com.company.ordermanagement.repository.OrderRepository;
import com.company.ordermanagement.repository.PaymentRepository;
import com.company.ordermanagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Payment processPayment(UUID orderId, BigDecimal amount, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .status(Payment.PaymentStatus.PENDING)
                .paymentMethod(paymentMethod)
                .correlationId(UUID.randomUUID())
                .build();

        // Simulate gateway call
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setGatewayReference("GW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        log.info("Payment processed: paymentId={}, orderId={}", payment.getId(), orderId);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    // Activity 2 historical bug reference:
    // The notes below describe the old failure mode, not the current state.
    // This method is intentionally fixed in the repository.
    // Payment is loaded via findByIdWithOrderAndLineItems which uses @EntityGraph
    // to join order and lineItems — this works fine.
    //
    // BUT: If called from a context where the Payment was loaded in a different
    // session (e.g., after deserialization from a retry queue), or if the
    // @EntityGraph is removed, Order.getLineItems() returns an uninitialized
    // Hibernate proxy. Calling .size() or iterating outside a transaction
    // causes LazyInitializationException.
    //
    // This is the bug from Activity 2 — see docs/prompts/activity2-bug-fix.md
    // Stack trace: PaymentServiceImpl.java:87 → Order.getLineItems() returns null
    // Affects: multi-item, partially-shipped orders where the payment was loaded
    // from a DLQ retry after the original session closed.
    //
    // FIX: Add @Transactional here AND add @EntityGraph on the repository method
    // (already present on findByIdWithOrderAndLineItems).
    public Payment processRefund(UUID paymentId) {
        Payment payment = paymentRepository.findByIdWithOrderAndLineItems(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));

        Order order = payment.getOrder();

        // Line 87 — this is where the NPE/LazyInitializationException occurs
        // when lineItems proxy is not initialized:
        BigDecimal refundableAmount = order.getLineItems().stream()  // LINE 87
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (refundableAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("No refundable line items found for order: " + order.getId());
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setAmount(refundableAmount.negate());
        log.info("Refund processed: paymentId={}, amount={}", paymentId, refundableAmount);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
    }
}
