package com.company.ordermanagement.service;

import com.company.ordermanagement.model.dto.response.InventoryAuditLogResponse;
import com.company.ordermanagement.model.dto.response.InventoryResponse;

import java.util.List;
import java.util.UUID;

public interface InventoryService {
    InventoryResponse getInventoryBySku(String sku);
    InventoryResponse getInventoryById(UUID id);
    List<InventoryResponse> getAllInventory();
    List<InventoryResponse> getLowStockItems();
    List<InventoryResponse> getInventoryByWarehouse(String warehouseLocation);
    InventoryResponse adjustQuantity(UUID id, int delta, String reason);
    InventoryResponse receiveStock(UUID id, int quantity, UUID purchaseOrderId);
    List<InventoryAuditLogResponse> getAuditLog(UUID inventoryItemId);
}
