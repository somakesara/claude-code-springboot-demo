package com.company.ordermanagement.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class InventoryResponse {
    private final UUID id;
    private final UUID productId;
    private final String productSku;
    private final String productName;
    private final int quantityOnHand;
    private final int quantityReserved;
    private final int availableQuantity;
    private final int reorderThreshold;
    private final boolean belowReorderThreshold;
    private final BigDecimal unitCost;
    private final String warehouseLocation;
    private final Instant updatedAt;
}
