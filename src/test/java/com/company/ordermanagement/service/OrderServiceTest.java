package com.company.ordermanagement.service;

import com.company.ordermanagement.exception.OrderTotalExceededException;
import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.mapper.OrderMapper;
import com.company.ordermanagement.model.dto.request.CreateOrderRequest;
import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.model.entity.Customer;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.model.event.OrderCreatedEvent;
import com.company.ordermanagement.repository.CustomerRepository;
import com.company.ordermanagement.repository.OrderRepository;
import com.company.ordermanagement.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "maxOrderTotal", new BigDecimal("50000"));
    }

    @Test
    void createOrder_validRequest_returnsOrderResponse() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .firstName("Alice").lastName("Johnson").tier(Customer.CustomerTier.VIP).build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(customerId)
                .lineItems(List.of(
                        CreateOrderRequest.LineItemRequest.builder()
                                .productId(UUID.randomUUID())
                                .productSku("WIDGET-001")
                                .productName("Standard Widget")
                                .quantity(3)
                                .unitPrice(new BigDecimal("19.99"))
                                .build()
                ))
                .build();

        Order savedOrder = Order.builder()
                .customer(customer)
                .status(Order.OrderStatus.PENDING)
                .total(new BigDecimal("59.97"))
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toResponse(savedOrder)).thenReturn(OrderResponse.builder()
                .status(Order.OrderStatus.PENDING)
                .total(new BigDecimal("59.97"))
                .build());

        OrderResponse result = orderService.createOrder(request);

        assertThat(result.getTotal()).isEqualByComparingTo("59.97");
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_unknownCustomer_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(customerRepository.findById(unknownId)).thenReturn(Optional.empty());

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(unknownId)
                .lineItems(List.of(
                        CreateOrderRequest.LineItemRequest.builder()
                                .productId(UUID.randomUUID())
                                .productSku("WIDGET-001")
                                .productName("Widget")
                                .quantity(1)
                                .unitPrice(BigDecimal.TEN)
                                .build()
                ))
                .build();

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer");
    }

    @Test
    void createOrder_totalExceedsLimit_throwsOrderTotalExceededException() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .firstName("Bob").lastName("Smith").tier(Customer.CustomerTier.STANDARD).build();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(customerId)
                .lineItems(List.of(
                        CreateOrderRequest.LineItemRequest.builder()
                                .productId(UUID.randomUUID())
                                .productSku("EXPENSIVE-001")
                                .productName("Very Expensive Item")
                                .quantity(1)
                                .unitPrice(new BigDecimal("60000"))
                                .build()
                ))
                .build();

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OrderTotalExceededException.class)
                .hasMessageContaining("60000");
    }

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

    @Test
    void createOrder_publishesEventAfterSave() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .firstName("Alice").lastName("Johnson").tier(Customer.CustomerTier.VIP).build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(orderMapper.toResponse(any())).thenReturn(OrderResponse.builder().build());

        CreateOrderRequest request = CreateOrderRequest.builder()
                .customerId(customerId)
                .lineItems(List.of(
                        CreateOrderRequest.LineItemRequest.builder()
                                .productId(UUID.randomUUID())
                                .productSku("WIDGET-001")
                                .productName("Widget")
                                .quantity(1)
                                .unitPrice(new BigDecimal("10.00"))
                                .build()
                ))
                .build();

        orderService.createOrder(request);

        // Verify event published once — not inside @Transactional, via ApplicationEventPublisher
        verify(eventPublisher, times(1)).publishEvent(any(OrderCreatedEvent.class));
    }
}
