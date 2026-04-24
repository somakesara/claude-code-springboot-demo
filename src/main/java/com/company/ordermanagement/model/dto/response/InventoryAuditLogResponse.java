package com.company.ordermanagement.model.dto.response;

import com.company.ordermanagement.model.entity.InventoryAuditLog;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class InventoryAuditLogResponse {
    private final UUID id;
    private final UUID inventoryItemId;
    private final String productSku;
    private final InventoryAuditLog.ChangeType changeType;
    private final int quantityBefore;
    private final int quantityAfter;
    private final int delta;
    private final UUID referenceId;
    private final String referenceType;
    private final String notes;
    private final Instant recordedAt;
    private final Instant createdAt;
}
