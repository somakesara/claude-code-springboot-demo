package com.company.ordermanagement.mapper;

import com.company.ordermanagement.model.dto.response.InventoryAuditLogResponse;
import com.company.ordermanagement.model.dto.response.InventoryResponse;
import com.company.ordermanagement.model.entity.InventoryAuditLog;
import com.company.ordermanagement.model.entity.InventoryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "availableQuantity", expression = "java(item.getAvailableQuantity())")
    @Mapping(target = "belowReorderThreshold", expression = "java(item.getAvailableQuantity() < item.getReorderThreshold())")
    InventoryResponse toResponse(InventoryItem item);

    InventoryAuditLogResponse toResponse(InventoryAuditLog auditLog);
}
