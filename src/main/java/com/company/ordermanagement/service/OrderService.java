package com.company.ordermanagement.service;

import com.company.ordermanagement.model.entity.Order;

import java.util.List;
import java.util.UUID;

// Activity 1 — add:
//   OrderResponse createOrder(CreateOrderRequest request);
//   OrderResponse getOrder(UUID orderId);
//   List<OrderResponse> getOrdersByCustomer(UUID customerId);
//   OrderResponse cancelOrder(UUID orderId);
public interface OrderService {
    Order getOrderEntity(UUID orderId);
    List<Order> getOrdersByCustomer(UUID customerId);
    Order cancelOrder(UUID orderId);
}
