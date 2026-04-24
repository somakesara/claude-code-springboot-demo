package com.company.ordermanagement.exception;

public class InsufficientInventoryException extends RuntimeException {
    public InsufficientInventoryException(String productSku, int requested, int available) {
        super("Insufficient inventory for product " + productSku
                + ": requested=" + requested + ", available=" + available);
    }
}
