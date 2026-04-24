package com.company.ordermanagement.model.event;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
public class OrderCreatedEvent extends BaseEvent {

    private final UUID orderId;
    private final UUID customerId;
    private final BigDecimal total;
    private final List<LineItemSnapshot> lineItems;

    @Getter
    @SuperBuilder
    public static class LineItemSnapshot extends BaseEvent {
        private final UUID productId;
        private final String productSku;
        private final int quantity;
        private final BigDecimal unitPrice;
    }
}
