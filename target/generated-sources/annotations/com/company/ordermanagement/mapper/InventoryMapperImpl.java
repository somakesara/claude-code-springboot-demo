package com.company.ordermanagement.mapper;

import com.company.ordermanagement.model.dto.response.InventoryAuditLogResponse;
import com.company.ordermanagement.model.dto.response.InventoryResponse;
import com.company.ordermanagement.model.entity.InventoryAuditLog;
import com.company.ordermanagement.model.entity.InventoryItem;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-24T06:32:00+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public InventoryResponse toResponse(InventoryItem item) {
        if ( item == null ) {
            return null;
        }

        InventoryResponse.InventoryResponseBuilder inventoryResponse = InventoryResponse.builder();

        inventoryResponse.id( item.getId() );
        inventoryResponse.productId( item.getProductId() );
        inventoryResponse.productName( item.getProductName() );
        inventoryResponse.productSku( item.getProductSku() );
        inventoryResponse.quantityOnHand( item.getQuantityOnHand() );
        inventoryResponse.quantityReserved( item.getQuantityReserved() );
        inventoryResponse.reorderThreshold( item.getReorderThreshold() );
        inventoryResponse.unitCost( item.getUnitCost() );
        inventoryResponse.updatedAt( item.getUpdatedAt() );
        inventoryResponse.warehouseLocation( item.getWarehouseLocation() );

        inventoryResponse.availableQuantity( item.getAvailableQuantity() );
        inventoryResponse.belowReorderThreshold( item.getAvailableQuantity() < item.getReorderThreshold() );

        return inventoryResponse.build();
    }

    @Override
    public InventoryAuditLogResponse toResponse(InventoryAuditLog auditLog) {
        if ( auditLog == null ) {
            return null;
        }

        InventoryAuditLogResponse.InventoryAuditLogResponseBuilder inventoryAuditLogResponse = InventoryAuditLogResponse.builder();

        inventoryAuditLogResponse.changeType( auditLog.getChangeType() );
        inventoryAuditLogResponse.createdAt( auditLog.getCreatedAt() );
        inventoryAuditLogResponse.delta( auditLog.getDelta() );
        inventoryAuditLogResponse.id( auditLog.getId() );
        inventoryAuditLogResponse.inventoryItemId( auditLog.getInventoryItemId() );
        inventoryAuditLogResponse.notes( auditLog.getNotes() );
        inventoryAuditLogResponse.productSku( auditLog.getProductSku() );
        inventoryAuditLogResponse.quantityAfter( auditLog.getQuantityAfter() );
        inventoryAuditLogResponse.quantityBefore( auditLog.getQuantityBefore() );
        inventoryAuditLogResponse.recordedAt( auditLog.getRecordedAt() );
        inventoryAuditLogResponse.referenceId( auditLog.getReferenceId() );
        inventoryAuditLogResponse.referenceType( auditLog.getReferenceType() );

        return inventoryAuditLogResponse.build();
    }
}
