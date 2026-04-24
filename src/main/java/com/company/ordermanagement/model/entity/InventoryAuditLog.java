package com.company.ordermanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryAuditLog extends BaseEntity {

    @Column(name = "inventory_item_id", nullable = false)
    private UUID inventoryItemId;

    @Column(name = "product_sku", nullable = false)
    private String productSku;

    @Column(name = "change_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChangeType changeType;

    @Column(name = "quantity_before", nullable = false)
    private int quantityBefore;

    @Column(name = "quantity_after", nullable = false)
    private int quantityAfter;

    @Column(name = "delta", nullable = false)
    private int delta;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "reference_type")
    private String referenceType;

    @Column(name = "notes")
    private String notes;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    public enum ChangeType {
        RECEIVED, RESERVED, RELEASED, ADJUSTED, SHIPPED, RETURNED, DAMAGED
    }
}
