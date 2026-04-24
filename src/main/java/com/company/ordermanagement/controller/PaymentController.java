package com.company.ordermanagement.controller;

import com.company.ordermanagement.model.dto.response.ApiResponse;
import com.company.ordermanagement.model.entity.Payment;
import com.company.ordermanagement.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Payment>> processPayment(
            @PathVariable UUID orderId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod) {
        Payment payment = paymentService.processPayment(orderId, amount, paymentMethod);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(payment));
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<Payment>> getPayment(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(ApiResponse.ok(paymentService.getPayment(paymentId)));
    }

    // Activity 2 — this endpoint triggers the bug in PaymentServiceImpl.processRefund()
    // Stack trace: PaymentController.java:45 → PaymentServiceImpl.java:87
    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Payment>> processRefund(   // line 45
            @PathVariable UUID paymentId) {
        Payment refund = paymentService.processRefund(paymentId);
        return ResponseEntity.ok(ApiResponse.ok(refund, "Refund processed"));
    }
}
