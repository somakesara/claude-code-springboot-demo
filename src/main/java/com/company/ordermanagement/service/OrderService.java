package com.company.ordermanagement.service;

import com.company.ordermanagement.model.dto.request.CreateOrderRequest;
import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.model.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse getOrder(UUID orderId);
    List<OrderResponse> getOrdersByCustomer(UUID customerId);
    OrderResponse cancelOrder(UUID orderId);
    Order getOrderEntity(UUID orderId);
}
