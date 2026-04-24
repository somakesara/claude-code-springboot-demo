package com.company.ordermanagement.controller;

import com.company.ordermanagement.model.dto.request.CreateOrderRequest;
import com.company.ordermanagement.model.dto.response.ApiResponse;
import com.company.ordermanagement.model.dto.response.OrderResponse;
import com.company.ordermanagement.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Order created successfully"));
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrder(orderId)));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByCustomer(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrdersByCustomer(customerId)));
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.cancelOrder(orderId), "Order cancelled"));
    }
}
