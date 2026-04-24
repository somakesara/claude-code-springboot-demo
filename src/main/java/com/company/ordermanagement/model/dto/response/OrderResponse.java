package com.company.ordermanagement.model.dto.response;

import com.company.ordermanagement.model.entity.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    private final UUID id;
    private final UUID customerId;
    private final String customerName;
    private final Order.OrderStatus status;
    private final BigDecimal total;
    private final List<LineItemResponse> lineItems;
    private final Instant createdAt;
    private final Instant updatedAt;

    @Getter
    @Builder
    public static class LineItemResponse {
        private final UUID id;
        private final UUID productId;
        private final String productSku;
        private final String productName;
        private final int quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal lineTotal;
    }
}
