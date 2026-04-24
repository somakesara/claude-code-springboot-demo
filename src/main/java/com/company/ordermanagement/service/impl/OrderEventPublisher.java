package com.company.ordermanagement.service.impl;

import com.company.ordermanagement.model.event.CustomerCreatedEvent;
import com.company.ordermanagement.model.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // @TransactionalEventListener fires AFTER the DB transaction commits.
    // This guarantees the DB write is durable before sending to Kafka.
    // If Kafka send fails here, it does NOT roll back the DB — idempotency
    // in the consumer handles duplicates. This is the correct pattern.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send("order-service.order.created",
                event.getOrderId().toString(), event);
        log.info("Published OrderCreatedEvent: orderId={}, correlationId={}",
                event.getOrderId(), event.getCorrelationId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCustomerCreated(CustomerCreatedEvent event) {
        kafkaTemplate.send("order-service.customer.created",
                event.getCustomerId().toString(), event);
        log.info("Published CustomerCreatedEvent: customerId={}",
                event.getCustomerId());
    }
}
