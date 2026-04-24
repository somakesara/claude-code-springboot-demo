package com.company.ordermanagement.exception;

import java.math.BigDecimal;

public class OrderTotalExceededException extends RuntimeException {
    public OrderTotalExceededException(BigDecimal total, BigDecimal limit) {
        super("Order total " + total + " exceeds maximum allowed limit of " + limit);
    }
}
