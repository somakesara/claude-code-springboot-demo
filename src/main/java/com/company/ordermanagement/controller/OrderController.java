package com.company.ordermanagement.controller;

import com.company.ordermanagement.model.dto.response.ApiResponse;
import com.company.ordermanagement.model.entity.Order;
import com.company.ordermanagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// Activity 1 — add POST /api/v1/orders endpoint here after creating CreateOrderRequest,
// OrderResponse, and OrderMapper

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderEntity(orderId)));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrdersByCustomer(customerId)));
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.cancelOrder(orderId), "Order cancelled"));
    }
}
