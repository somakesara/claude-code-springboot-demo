package com.company.ordermanagement.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem extends BaseEntity {

    @Column(name = "product_id", nullable = false, unique = true)
    private java.util.UUID productId;

    @Column(name = "product_sku", nullable = false, unique = true)
    private String productSku;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "quantity_reserved", nullable = false)
    private int quantityReserved;

    @Column(name = "reorder_threshold", nullable = false)
    private int reorderThreshold;

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "warehouse_location")
    private String warehouseLocation;

    public int getAvailableQuantity() {
        return quantityOnHand - quantityReserved;
    }
}
