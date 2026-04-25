package com.company.ordermanagement.service;

import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.mapper.OrderMapper;
import com.company.ordermanagement.model.entity.Customer;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.repository.OrderRepository;
import com.company.ordermanagement.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// Activity 1 — add createOrder tests here after implementing the feature

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void cancelOrder_shippedOrder_throwsIllegalStateException() {
        UUID orderId = UUID.randomUUID();
        Order shippedOrder = Order.builder()
                .status(Order.OrderStatus.SHIPPED)
                .customer(Customer.builder().firstName("A").lastName("B").build())
                .total(BigDecimal.TEN)
                .build();

        when(orderRepository.findByIdWithLineItems(orderId)).thenReturn(Optional.of(shippedOrder));

        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("SHIPPED");
    }

    @Test
    void getOrder_unknownId_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(orderRepository.findByIdWithLineItems(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(unknownId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order");
    }
}
