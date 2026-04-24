package com.company.ordermanagement.service.impl;

import com.company.ordermanagement.exception.OrderTotalExceededException;
import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.mapper.OrderMapper;
import com.company.ordermanagement.model.dto.request.CreateOrderRequest;
import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.model.entity.Customer;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.model.entity.OrderItem;
import com.company.ordermanagement.model.event.OrderCreatedEvent;
import com.company.ordermanagement.repository.CustomerRepository;
import com.company.ordermanagement.repository.OrderRepository;
import com.company.ordermanagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderMapper orderMapper;

    @Value("${order.max-total:50000}")
    private BigDecimal maxOrderTotal;

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));

        Order order = Order.builder()
                .customer(customer)
                .status(Order.OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .correlationId(UUID.randomUUID())
                .build();

        for (CreateOrderRequest.LineItemRequest itemReq : request.getLineItems()) {
            OrderItem item = OrderItem.builder()
                    .productId(itemReq.getProductId())
                    .productSku(itemReq.getProductSku())
                    .productName(itemReq.getProductName())
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .build();
            order.addLineItem(item);
        }

        if (order.getTotal().compareTo(maxOrderTotal) > 0) {
            throw new OrderTotalExceededException(order.getTotal(), maxOrderTotal);
        }

        Order saved = orderRepository.save(order);
        log.info("Order created: orderId={}, customerId={}, total={}",
                saved.getId(), saved.getCustomer().getId(), saved.getTotal());

        // Publish AFTER commit — ApplicationEventPublisher + @TransactionalEventListener
        // ensures Kafka is only called once the DB transaction has successfully committed.
        // Never call kafkaTemplate.send() directly inside @Transactional — if the DB rolls
        // back the message is already sent.
        eventPublisher.publishEvent(buildOrderCreatedEvent(saved));

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        Order order = orderRepository.findByIdWithLineItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(UUID customerId) {
        return orderRepository.findAllByCustomerIdWithLineItems(customerId).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID orderId) {
        Order order = orderRepository.findByIdWithLineItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getStatus() == Order.OrderStatus.SHIPPED
                || order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                    "Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        log.info("Order cancelled: orderId={}", orderId);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderEntity(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    private OrderCreatedEvent buildOrderCreatedEvent(Order order) {
        List<OrderCreatedEvent.LineItemSnapshot> snapshots = order.getLineItems().stream()
                .map(item -> OrderCreatedEvent.LineItemSnapshot.builder()
                        .eventId(UUID.randomUUID())
                        .timestamp(Instant.now())
                        .eventType("LINE_ITEM_SNAPSHOT")
                        .correlationId(order.getCorrelationId())
                        .productId(item.getProductId())
                        .productSku(item.getProductSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .timestamp(Instant.now())
                .eventType("ORDER_CREATED")
                .correlationId(order.getCorrelationId())
                .orderId(order.getId())
                .customerId(order.getCustomer().getId())
                .total(order.getTotal())
                .lineItems(snapshots)
                .build();
    }
}
