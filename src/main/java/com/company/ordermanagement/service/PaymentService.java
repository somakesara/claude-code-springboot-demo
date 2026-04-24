package com.company.ordermanagement.service;

import com.company.ordermanagement.model.dto.response.ApiResponse;
import com.company.ordermanagement.model.entity.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    Payment processPayment(UUID orderId, BigDecimal amount, String paymentMethod);
    Payment processRefund(UUID paymentId);
    Payment getPayment(UUID paymentId);
}
