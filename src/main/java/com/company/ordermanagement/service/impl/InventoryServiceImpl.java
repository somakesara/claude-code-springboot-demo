package com.company.ordermanagement.service.impl;

import com.company.ordermanagement.exception.ResourceNotFoundException;
import com.company.ordermanagement.mapper.InventoryMapper;
import com.company.ordermanagement.model.dto.response.InventoryAuditLogResponse;
import com.company.ordermanagement.model.dto.response.InventoryResponse;
import com.company.ordermanagement.model.entity.InventoryAuditLog;
import com.company.ordermanagement.model.entity.InventoryItem;
import com.company.ordermanagement.repository.InventoryAuditLogRepository;
import com.company.ordermanagement.repository.InventoryRepository;
import com.company.ordermanagement.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryAuditLogRepository auditLogRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryBySku(String sku) {
        InventoryItem item = inventoryRepository.findByProductSkuAndDeletedFalse(sku)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem with SKU: " + sku));
        return inventoryMapper.toResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(UUID id) {
        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", id));
        return inventoryMapper.toResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAllActive().stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepository.findAllBelowReorderThreshold().stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByWarehouse(String warehouseLocation) {
        return inventoryRepository.findAllByWarehouseLocation(warehouseLocation).stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponse adjustQuantity(UUID id, int delta, String reason) {
        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", id));

        int before = item.getQuantityOnHand();
        item.setQuantityOnHand(before + delta);

        InventoryAuditLog log = InventoryAuditLog.builder()
                .inventoryItemId(id)
                .productSku(item.getProductSku())
                .changeType(delta > 0 ? InventoryAuditLog.ChangeType.ADJUSTED : InventoryAuditLog.ChangeType.ADJUSTED)
                .quantityBefore(before)
                .quantityAfter(item.getQuantityOnHand())
                .delta(delta)
                .notes(reason)
                .recordedAt(Instant.now())
                .build();

        auditLogRepository.save(log);
        InventoryItem saved = inventoryRepository.save(item);
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse receiveStock(UUID id, int quantity, UUID purchaseOrderId) {
        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryItem", id));

        int before = item.getQuantityOnHand();
        item.setQuantityOnHand(before + quantity);

        InventoryAuditLog log = InventoryAuditLog.builder()
                .inventoryItemId(id)
                .productSku(item.getProductSku())
                .changeType(InventoryAuditLog.ChangeType.RECEIVED)
                .quantityBefore(before)
                .quantityAfter(item.getQuantityOnHand())
                .delta(quantity)
                .referenceId(purchaseOrderId)
                .referenceType("PURCHASE_ORDER")
                .recordedAt(Instant.now())
                .build();

        auditLogRepository.save(log);
        InventoryItem saved = inventoryRepository.save(item);
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryAuditLogResponse> getAuditLog(UUID inventoryItemId) {
        return auditLogRepository.findByInventoryItemId(inventoryItemId).stream()
                .map(inventoryMapper::toResponse)
                .toList();
    }
}
